package com.example.backend.controller; // 替换成你自己的包名

import com.example.backend.entity.DetectionTask;
import com.example.backend.repository.DetectionTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api1")
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*", methods =
        {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}) // 允许前端地址（根据实际前端端口修改）
public class VideoProcessController {
    @Autowired
    private DetectionTaskRepository repo;

    // Python 解释器命令
    private static final String PYTHON_COMMAND = "python";
    // Python 脚本的绝对路径
    private static final String SCRIPT_PATH = "D:/JavaKnowledge/JAVA项目/Drone1/yolo_process.py";
    // ================================================
    @Transactional
    @PostMapping("/process-video")
    public ResponseEntity<Resource> processVideo(@RequestParam("video") MultipartFile videoFile) {
        System.out.println("我进来了！");
        Path inputPath = null;
        Path outputPath = null;

        try {
            // 1. 保存上传的视频到临时文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String uniqueId = UUID.randomUUID().toString();
            inputPath = Path.of(tempDir, "input_" + uniqueId + ".mp4");
            outputPath = Path.of(tempDir, "output_" + uniqueId + ".mp4");
            System.out.println("B: 准备保存上传文件");
            Files.copy(videoFile.getInputStream(), inputPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("C: 文件保存完成");
            // 2. 构建 ProcessBuilder 执行 Python 脚本
            System.out.println("D: 准备启动 Python");
            ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_COMMAND,
                    SCRIPT_PATH,
                    inputPath.toString(),
                    outputPath.toString()
            );

            pb.directory(new File(System.getProperty("java.io.tmpdir")));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            System.out.println("E: Python 已启动");
            // 读取子进程输出（避免缓冲区阻塞）
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println(line);
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
// ⭐ 检测完成，保存检测任务
            DetectionTask task = new DetectionTask();
            task.setVideoName(videoFile.getOriginalFilename());
            task.setStartTime(LocalDateTime.now().minusSeconds(1)); // 可按需更精确
            task.setEndTime(LocalDateTime.now());
            task.setCreatedDate(LocalDate.now()); // ⭐ 关键生命线
            System.out.println(">>> 准备保存 DetectionTask");
            repo.save(task);
            System.out.println(">>> DetectionTask 已调用 save()");
            System.out.println("DB 写入完成");
           /* // 3. 读取处理后的视频文件
            byte[] videoBytes = Files.readAllBytes(outputPath);

            // 4. 设置响应头，返回视频流
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentDispositionFormData("attachment", "processed_video.mp4");
            headers.setContentLength(videoBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoBytes);
*/

            // 3. 检查输出文件是否存在且不为空
            if (!Files.exists(outputPath) || Files.size(outputPath) == 0) {
                throw new RuntimeException("处理后的视频文件无效");
            }

// 4. 使用 Resource 返回文件
            Resource resource = new FileSystemResource(outputPath.toFile());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    //.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"processed_video.mp4\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } finally {
            // 5. 清理临时文件
            // 在 finally 块中注释删除代码，或只删除 input，保留 output
            System.out.println("输出文件路径: " + outputPath);
        }
    }
}