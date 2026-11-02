package com.example.backend.controller;

import com.example.backend.pojo.DetectionTask;
import com.example.backend.service.DetectionTaskService;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detection")
public class DetectionTaskController {

    private final DetectionTaskService detectionTaskService;

    public DetectionTaskController(DetectionTaskService detectionTaskService) {
        this.detectionTaskService = detectionTaskService;
    }

    /** 开始检测，创建任务记录 */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startDetection(@RequestBody Map<String, String> payload) {
        Long taskId = detectionTaskService.startTask(payload.get("videoName"), payload.get("startTime"));
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        return ResponseEntity.ok(response);
    }

    /** 结束检测，更新结束时间 */
    @PostMapping("/end")
    public ResponseEntity<Void> endDetection(@RequestBody Map<String, Object> payload) {
        Long taskId = Long.valueOf(payload.get("taskId").toString());
        detectionTaskService.endTask(taskId, (String) payload.get("endTime"));
        return ResponseEntity.ok().build();
    }

    /** 上传任务的结果视频 */
    @PostMapping("/upload-result/{taskId}")
    public ResponseEntity<String> uploadResultVideo(
            @PathVariable Long taskId,
            @RequestParam("video") MultipartFile videoFile) {
        try {
            detectionTaskService.saveResultVideo(taskId, videoFile);
            return ResponseEntity.ok("上传成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
        }
    }

    /** 获取任务的结果视频 */
    @GetMapping("/video/{taskId}")
    public ResponseEntity<Resource> getResultVideo(@PathVariable Long taskId) {
        Resource resource = detectionTaskService.getResultVideo(taskId);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/webm"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"result.webm\"")
                .body(resource);
    }

    /** 统计近N天每日检测次数 */
    @GetMapping("/stats")
    public List<Map<String, Object>> getStats(@RequestParam(defaultValue = "30") int days) {
        return detectionTaskService.getStats(days);
    }

    /** 查询某一天的所有检测记录 */
    @GetMapping("/list")
    public List<DetectionTask> listByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return detectionTaskService.listByDate(date);
    }
}
