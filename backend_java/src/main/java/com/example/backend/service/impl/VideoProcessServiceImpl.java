package com.example.backend.service.impl;

import com.example.backend.mapper.DetectionTaskMapper;
import com.example.backend.pojo.DetectionTask;
import com.example.backend.service.VideoProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 视频处理业务实现：调用外部 Python YOLO 脚本处理视频
 */
@Service
public class VideoProcessServiceImpl implements VideoProcessService {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessServiceImpl.class);

    private final DetectionTaskMapper detectionTaskMapper;

    /** Python 解释器命令与脚本路径（在 application.properties 中配置） */
    @Value("${app.python.command:python}")
    private String pythonCommand;

    @Value("${app.python.script-path:yolo_process.py}")
    private String scriptPath;

    public VideoProcessServiceImpl(DetectionTaskMapper detectionTaskMapper) {
        this.detectionTaskMapper = detectionTaskMapper;
    }

    @Override
    @Transactional
    public Resource processVideo(MultipartFile videoFile) {
        Path inputPath = null;
        Path outputPath = null;

        try {
            // 1. 保存上传的视频到临时文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String uniqueId = UUID.randomUUID().toString();
            inputPath = Path.of(tempDir, "input_" + uniqueId + ".mp4");
            outputPath = Path.of(tempDir, "output_" + uniqueId + ".mp4");
            Files.copy(videoFile.getInputStream(), inputPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("上传文件已保存到临时目录: {}", inputPath);

            // 2. 构建 ProcessBuilder 执行 Python 脚本
            ProcessBuilder pb = new ProcessBuilder(
                    pythonCommand,
                    scriptPath,
                    inputPath.toString(),
                    outputPath.toString()
            );
            pb.directory(new File(System.getProperty("java.io.tmpdir")));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            log.info("Python 进程已启动");

            // 读取子进程输出（避免缓冲区阻塞）
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.info("[python] {}", line);
                }
            }

            // 等待进程结束（超时 10 分钟）
            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("视频处理超时（10分钟）");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("Python 脚本执行失败，退出码：" + exitCode + "\n输出：" + output);
            }

            // 检测完成，保存检测任务
            DetectionTask task = new DetectionTask();
            task.setVideoName(videoFile.getOriginalFilename());
            task.setStartTime(LocalDateTime.now().minusSeconds(1)); // 可按需更精确
            task.setEndTime(LocalDateTime.now());
            task.setCreatedDate(LocalDate.now());
            detectionTaskMapper.insert(task);
            log.info("检测任务已写入数据库, id={}", task.getId());

            // 3. 检查输出文件是否存在且不为空
            if (!Files.exists(outputPath) || Files.size(outputPath) == 0) {
                throw new RuntimeException("处理后的视频文件无效");
            }

            // 4. 使用 Resource 返回文件
            return new FileSystemResource(outputPath.toFile());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("视频处理失败", e);
        } finally {
            // 临时文件保留在系统临时目录，便于排查；如需清理可在此删除
            if (outputPath != null) {
                log.info("输出文件路径: {}", outputPath);
            }
        }
    }
}
