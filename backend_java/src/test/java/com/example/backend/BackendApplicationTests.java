package com.example.backend;

import com.example.backend.entity.DetectionTask;
import com.example.backend.repository.DetectionTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class BackendApplicationTests {

    @Bean
    CommandLineRunner init(DetectionTaskRepository repo) {
        return args -> {
            DetectionTask task = new DetectionTask();
            task.setVideoName("test.mp4");
            task.setStartTime(LocalDateTime.now());
            task.setCreatedDate(LocalDate.now());
            repo.save(task);
            System.out.println("✅ 数据插入成功");
        };
    }

    @Test
	void contextLoads() {
	}

}

