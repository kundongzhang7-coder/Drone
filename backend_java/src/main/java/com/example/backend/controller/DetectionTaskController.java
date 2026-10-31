package com.example.backend.controller;

import com.example.backend.entity.DetectionTask;
import com.example.backend.repository.DetectionTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detection")
@CrossOrigin(origins = "http://localhost:5173")
public class DetectionTaskController {

    @Autowired
    private DetectionTaskRepository repository;

    // 开始检测，创建任务记录
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startDetection(@RequestBody Map<String, String> payload) {
        String videoName = payload.get("videoName");
        String startTimeStr = payload.get("startTime");

        // 解析带时区的时间字符串
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse(startTimeStr),
                ZoneId.systemDefault()
        );

        DetectionTask task = new DetectionTask();
        task.setVideoName(videoName);
        task.setStartTime(startTime);
        task.setCreatedDate(LocalDate.now());

        DetectionTask saved = repository.save(task);

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", saved.getId());
        return ResponseEntity.ok(response);
    }


    // 结束检测，更新结束时间
    @PostMapping("/end")
    public ResponseEntity<Void> endDetection(@RequestBody Map<String, Object> payload) {
        Long taskId = Long.valueOf(payload.get("taskId").toString());
        String endTimeStr = (String) payload.get("endTime");

        LocalDateTime endTime = LocalDateTime.ofInstant(
                Instant.parse(endTimeStr),
                ZoneId.systemDefault()
        );

        DetectionTask task = repository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        task.setEndTime(endTime);
        repository.save(task);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/upload-result/{taskId}")
    public ResponseEntity<String> uploadResultVideo(
            @PathVariable Long taskId,
            @RequestParam("video") MultipartFile videoFile) {
        System.out.println("接收到上传请求，taskId=" + taskId + ", 文件名=" + videoFile.getOriginalFilename() + ", 大小=" + videoFile.getSize());
        try {
            // 检查任务是否存在
            DetectionTask task = repository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("任务不存在"));

            // 定义存储目录（例如项目根目录下的 videos 文件夹）
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
            repository.save(task);

            return ResponseEntity.ok("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
        }
    }
    @GetMapping("/video/{taskId}")
    public ResponseEntity<Resource> getResultVideo(@PathVariable Long taskId) {
        DetectionTask task = repository.findById(taskId).orElse(null);
        if (task == null || task.getVideoResultPath() == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(task.getVideoResultPath());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/webm"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"result.webm\"")
                .body(resource);
    }
    // 统计近N天每日检测次数
    @GetMapping("/stats")
    public List<Map<String, Object>> getStats(@RequestParam(defaultValue = "30") int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        return repository.countByDateRange(startDate);
    }

    // 查询某一天的所有检测记录
    @GetMapping("/list")
    public List<DetectionTask> listByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return repository.findByCreatedDateOrderByStartTimeDesc(date);
    }
}