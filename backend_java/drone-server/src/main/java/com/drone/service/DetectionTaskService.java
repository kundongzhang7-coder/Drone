package com.drone.service;

import com.drone.common.result.PageResult;
import com.drone.mq.DetectionMessage;
import com.drone.pojo.dto.TaskQueryDTO;
import com.drone.pojo.entity.DetectionTask;
import com.drone.pojo.vo.DetectionStatsVO;
import com.drone.pojo.vo.TaskVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 检测任务服务
 */
public interface DetectionTaskService {

    DetectionTask startTask(String sourceType, String taskName);

    void endTask(Long taskId, DetectionTask params);

    String uploadResult(Long taskId, MultipartFile file);

    PageResult<TaskVO> pageTasks(TaskQueryDTO query);

    DetectionStatsVO getStats();

    TaskVO getTaskById(Long id);

    void submitOfflineDetection(MultipartFile file, String taskName);

    void processOffline(DetectionMessage message);

    void markFailed(Long taskId);
}
