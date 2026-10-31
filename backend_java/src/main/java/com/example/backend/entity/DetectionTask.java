package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "detection_task")
public class DetectionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate createdDate;

    // 新增字段：处理后的视频文件路径
    private String videoResultPath;

    // 无参构造
    public DetectionTask() {}

    // Getter 和 Setter（包括新增字段）
    public String getVideoResultPath() {
        return videoResultPath;
    }
    public void setVideoResultPath(String videoResultPath) {
        this.videoResultPath = videoResultPath;
    }
    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVideoName() { return videoName; }
    public void setVideoName(String videoName) { this.videoName = videoName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}