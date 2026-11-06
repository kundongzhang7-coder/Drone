package com.drone.service.impl;

import cn.hutool.core.util.StrUtil;
import com.drone.common.constant.RedisConstants;
import com.drone.common.context.UserContext;
import com.drone.common.exception.BusinessException;
import com.drone.common.result.PageResult;
import com.drone.common.result.ResultCode;
import com.drone.lock.CacheClient;
import com.drone.lock.TaskBloomFilter;
import com.drone.mq.DetectionMessage;
import com.drone.mq.DetectionMessageProducer;
import com.drone.mapper.DetectionTaskMapper;
import com.drone.pojo.dto.TaskQueryDTO;
import com.drone.pojo.entity.DetectionTask;
import com.drone.pojo.vo.DetectionStatsVO;
import com.drone.pojo.vo.TaskVO;
import com.drone.service.CosService;
import com.drone.service.DetectionTaskService;
import com.drone.service.PythonDetectionResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.drone.common.constant.RedisConstants.*;

/**
 * 检测任务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionTaskServiceImpl implements DetectionTaskService {

    private final DetectionTaskMapper taskMapper;
    private final CacheClient cacheClient;
    private final TaskBloomFilter bloomFilter;
    private final CosService cosService;
    private final DetectionMessageProducer messageProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${drone.python.base-url:http://localhost:8000}")
    private String pythonBaseUrl;

    private RestClient restClient;

    @PostConstruct
    public void initRestClient() {
        this.restClient = RestClient.builder().baseUrl(pythonBaseUrl).build();
    }

    @Override
    public DetectionTask startTask(String sourceType, String taskName) {
        DetectionTask task = new DetectionTask();
        task.setTaskName(StrUtil.isBlank(taskName)
                ? "检测任务-" + System.currentTimeMillis() : taskName);
        task.setUserId(UserContext.getUserId());
        task.setStatus("PROCESSING");
        task.setSourceType(StrUtil.isBlank(sourceType) ? "REALTIME" : sourceType);
        task.setStartTime(LocalDateTime.now());
        taskMapper.insert(task);
        bloomFilter.add(task.getId());
        return task;
    }

    @Override
    public void endTask(Long taskId, DetectionTask params) {
        ensureTaskExists(taskId);
        DetectionTask update = new DetectionTask();
        update.setId(taskId);
        update.setStatus("COMPLETED");
        update.setEndTime(LocalDateTime.now());
        if (params != null) {
            update.setTotalFrames(params.getTotalFrames());
            update.setDroneCount(params.getDroneCount());
        }
        taskMapper.update(update);
        evictTaskCache(taskId);
    }

    @Override
    public String uploadResult(Long taskId, MultipartFile file) {
        ensureTaskExists(taskId);
        String url = cosService.upload(file, "video");
        DetectionTask update = new DetectionTask();
        update.setId(taskId);
        update.setResultVideoUrl(url);
        taskMapper.update(update);
        evictTaskCache(taskId);
        return url;
    }

    @Override
    public PageResult<TaskVO> pageTasks(TaskQueryDTO query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        int offset = (page - 1) * size;

        Long total = taskMapper.countList(query);
        List<DetectionTask> list = taskMapper.selectPage(query, offset, size);
        List<TaskVO> records = list.stream().map(this::toTaskVO).toList();
        return new PageResult<>(total, records);
    }

    @Override
    public DetectionStatsVO getStats() {
        // 统计结果走缓存，随机 TTL 防雪崩
        return cacheClient.queryWithPassThrough(
                RedisConstants.CACHE_STATS_KEY, "global", DetectionStatsVO.class,
                id -> loadStatsFromDb(), RedisConstants.CACHE_BASE_TTL);
    }

    @Override
    public TaskVO getTaskById(Long id) {
        // 布隆过滤器先拦截一定不存在的 id
        if (!bloomFilter.mightContain(id)) {
            return null;
        }
        DetectionTask task = cacheClient.queryWithMutex(
                CACHE_TASK_KEY, id, DetectionTask.class,
                taskMapper::findById, CACHE_BASE_TTL, LOCK_TASK_KEY);
        return task == null ? null : toTaskVO(task);
    }

    @Override
    public void submitOfflineDetection(MultipartFile file, String taskName) {
        // 1. 源视频上传 COS
        String sourceUrl = cosService.upload(file, "source");
        // 2. 建立处理中任务
        DetectionTask task = startTask("VIDEO", taskName);
        // 3. 投递消息，由消费者异步处理，实现削峰解耦
        DetectionMessage message = new DetectionMessage(
                task.getId(), task.getUserId(), sourceUrl, UUID.randomUUID().toString());
        messageProducer.send(message);
    }

    @Override
    public void processOffline(DetectionMessage message) {
        Long taskId = message.getTaskId();
        try {
            Map<String, Object> body = Map.of(
                    "taskId", taskId,
                    "videoUrl", message.getVideoUrl() == null ? "" : message.getVideoUrl());

            PythonDetectionResult result = restClient.post()
                    .uri("/detect/offline")
                    .body(body)
                    .retrieve()
                    .body(PythonDetectionResult.class);

            DetectionTask update = new DetectionTask();
            update.setId(taskId);
            update.setStatus("COMPLETED");
            update.setEndTime(LocalDateTime.now());
            if (result != null) {
                update.setResultVideoUrl(result.resultVideoUrl());
                update.setTotalFrames(result.totalFrames());
                update.setDroneCount(result.droneCount());
            }
            taskMapper.update(update);
            evictTaskCache(taskId);
        } catch (Exception e) {
            log.error("离线检测处理失败 taskId={}", taskId, e);
            // 抛出异常使消息进入死信队列，避免静默丢失
            throw new BusinessException("离线检测处理失败: " + e.getMessage());
        }
    }

    @Override
    public void markFailed(Long taskId) {
        DetectionTask update = new DetectionTask();
        update.setId(taskId);
        update.setStatus("FAILED");
        update.setEndTime(LocalDateTime.now());
        taskMapper.update(update);
        evictTaskCache(taskId);
    }

    private DetectionStatsVO loadStatsFromDb() {
        DetectionStatsVO vo = new DetectionStatsVO();
        vo.setTotalTasks(taskMapper.countAll());
        vo.setCompletedTasks(taskMapper.countByStatus("COMPLETED"));
        vo.setProcessingTasks(taskMapper.countByStatus("PROCESSING"));
        vo.setTotalDrones(taskMapper.sumDroneCount());
        vo.setTotalFrames(taskMapper.sumTotalFrames());
        return vo;
    }

    private void ensureTaskExists(Long taskId) {
        if (taskMapper.findById(taskId) == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
    }

    private void evictTaskCache(Long taskId) {
        redisTemplate.delete(CACHE_TASK_KEY + taskId);
    }

    private TaskVO toTaskVO(DetectionTask task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
}
