package com.drone.controller;

import com.drone.common.result.PageResult;
import com.drone.common.result.Result;
import com.drone.pojo.dto.TaskQueryDTO;
import com.drone.pojo.entity.DetectionTask;
import com.drone.pojo.vo.DetectionStatsVO;
import com.drone.pojo.vo.TaskVO;
import com.drone.service.DetectionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 检测任务控制器
 */
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class DetectionTaskController {

    private final DetectionTaskService detectionTaskService;

    @PostMapping("/start")
    public Result<DetectionTask> start(@RequestParam(required = false) String sourceType,
                                       @RequestParam(required = false) String taskName) {
        return Result.success(detectionTaskService.startTask(sourceType, taskName));
    }

    @PutMapping("/end/{id}")
    public Result<Void> end(@PathVariable Long id, @RequestBody(required = false) DetectionTask params) {
        detectionTaskService.endTask(id, params);
        return Result.success();
    }

    @PostMapping("/result/{id}")
    public Result<String> uploadResult(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file) {
        return Result.success(detectionTaskService.uploadResult(id, file));
    }

    @GetMapping("/list")
    public Result<PageResult<TaskVO>> list(@ModelAttribute TaskQueryDTO query) {
        return Result.success(detectionTaskService.pageTasks(query));
    }

    @GetMapping("/stats")
    public Result<DetectionStatsVO> stats() {
        return Result.success(detectionTaskService.getStats());
    }

    @GetMapping("/{id}")
    public Result<TaskVO> detail(@PathVariable Long id) {
        TaskVO task = detectionTaskService.getTaskById(id);
        return task == null ? Result.error("任务不存在") : Result.success(task);
    }

    @PostMapping("/offline")
    public Result<Void> offline(@RequestParam("file") MultipartFile file,
                                @RequestParam(required = false) String taskName) {
        detectionTaskService.submitOfflineDetection(file, taskName);
        return Result.success();
    }
}
