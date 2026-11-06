package com.drone.service;

import com.drone.common.exception.BusinessException;
import com.drone.common.properties.CosProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 腾讯云 COS 存储服务：统一负责头像、结果视频等文件上传，返回可访问 URL。
 * 数据库仅保存 URL，不保存二进制文件。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CosService {

    private final COSClient cosClient;
    private final CosProperties cosProperties;

    /**
     * 上传 MultipartFile
     *
     * @param file      前端上传的文件
     * @param directory 逻辑目录，如 avatar、video
     */
    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String extension = resolveExtension(file.getOriginalFilename());
        String key = buildKey(directory, extension);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        try (InputStream in = file.getInputStream()) {
            cosClient.putObject(cosProperties.getBucket(), key, in, metadata);
        } catch (IOException e) {
            log.error("COS 上传失败", e);
            throw new BusinessException("文件上传失败");
        }
        return cosProperties.getBaseUrl() + "/" + key;
    }

    /**
     * 上传字节数组（用于服务端生成的结果视频等）
     */
    public String uploadBytes(byte[] bytes, String directory, String extension, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new BusinessException("上传内容不能为空");
        }
        String key = buildKey(directory, extension);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(bytes.length);
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            cosClient.putObject(cosProperties.getBucket(), key, in, metadata);
        } catch (IOException e) {
            log.error("COS 字节上传失败", e);
            throw new BusinessException("文件上传失败");
        }
        return cosProperties.getBaseUrl() + "/" + key;
    }

    private String buildKey(String directory, String extension) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return directory + "/" + datePart + "/" + uuid + extension;
    }

    private String resolveExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
