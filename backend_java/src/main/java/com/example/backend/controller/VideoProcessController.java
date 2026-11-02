package com.example.backend.controller;

import com.example.backend.service.VideoProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api1")
public class VideoProcessController {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessController.class);

    private final VideoProcessService videoProcessService;

    public VideoProcessController(VideoProcessService videoProcessService) {
        this.videoProcessService = videoProcessService;
    }

    /** 上传视频 → 调用 Python 检测 → 返回处理后的视频 */
    @PostMapping("/process-video")
    public ResponseEntity<Resource> processVideo(@RequestParam("video") MultipartFile videoFile) {
        try {
            Resource resource = videoProcessService.processVideo(videoFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            log.error("视频处理失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
