package com.example.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频处理业务接口（调用 Python YOLO 脚本）
 */
public interface VideoProcessService {

    /**
     * 处理上传的视频：保存临时文件 → 调用 Python 脚本 → 返回处理后的视频资源
     *
     * @param videoFile 上传的视频
     * @return 处理后的视频资源
     */
    Resource processVideo(MultipartFile videoFile);
}
