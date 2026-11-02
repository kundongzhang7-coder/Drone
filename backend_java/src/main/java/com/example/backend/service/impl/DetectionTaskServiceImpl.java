package com.example.backend.service.impl;

import com.example.backend.mapper.DetectionTaskMapper;
import com.example.backend.pojo.DetectionTask;
import com.example.backend.service.DetectionTaskService;
import com.example.backend.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 检测任务业务实现
 */
@Service
public class DetectionTaskServiceImpl implements DetectionTaskService {

    private static final Logger log = LoggerFactory.getLogger(DetectionTaskServiceImpl.class);

    private final DetectionTaskMapper detectionTaskMapper;

    public DetectionTaskServiceImpl(DetectionTaskMapper detectionTaskMapper) {
        this.detectionTaskMapper = detectionTaskMapper;
    }

    @Override
    public Long startTask(String videoName, String startTimeStr) {
        DetectionTask task = new DetectionTask();
        task.setVideoName(videoName);
        task.setStartTime(TimeUtils.parseIsoToLocal(startTimeStr));
        task.setCreatedDate(LocalDate.now());
        detectionTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void endTask(Long taskId, String endTimeStr) {
        DetectionTask task = detectionTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        task.setEndTime(TimeUtils.parseIsoToLocal(endTimeStr));
        detectionTaskMapper.update(task);
    }

    @Override
    @Transactional
    public void saveResultVideo(Long taskId, MultipartFile videoFile) {
        log.info("接收到上传请求，taskId={}, 文件名={}, 大小={}",
                taskId, videoFile.getOriginalFilename(), videoFile.getSize());
        try {
            // 检查任务是否存在
            DetectionTask task = detectionTaskMapper.selectById(taskId);
            if (task == null) {
                throw new RuntimeException("任务不存在");
            }

            // 定义存储目录（项目运行目录下的 videos 文件夹）
            String uploadDir = System.getProperty("user.dir") + "/videos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 生成唯一文件名
            String fileName = taskId + "_" + System.currentTimeMillis() + ".webm";
            Path filePath = Paths.get(uploadDir + fileName);

            // 保存文件
            Files.copy(videoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 更新任务记录
            task.setVideoResultPath(filePath.toString());
            detectionTaskMapper.update(task);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("上传失败", e);
        }
    }

    @Override
    public Resource getResultVideo(Long taskId) {
        DetectionTask task = detectionTaskMapper.selectById(taskId);
        if (task == null || task.getVideoResultPath() == null) {
            return null;
        }
        Resource resource = new FileSystemResource(Paths.get(task.getVideoResultPath()));
        return resource.exists() ? resource : null;
    }

    @Override
    public List<Map<String, Object>> getStats(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1L);
        return detectionTaskMapper.countByDateRange(startDate);
    }

    @Override
    public List<DetectionTask> listByDate(LocalDate date) {
        return detectionTaskMapper.selectByCreatedDate(date);
    }
}
