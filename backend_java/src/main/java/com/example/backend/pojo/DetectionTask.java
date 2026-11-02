package com.example.backend.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 检测任务实体类，对应数据表 detection_task
 * （数据库下划线命名 ↔ Java 驼峰命名由 MyBatis 自动映射）
 */
public class DetectionTask {

    private Long id;

    /** 视频名称 */
    private String videoName;

    /** 检测开始时间 */
    private LocalDateTime startTime;

    /** 检测结束时间 */
    private LocalDateTime endTime;

    /** 任务创建日期（用于按天统计） */
    private LocalDate createdDate;

    /** 处理后的结果视频文件路径 */
    private String videoResultPath;

    public DetectionTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getVideoResultPath() {
        return videoResultPath;
    }

    public void setVideoResultPath(String videoResultPath) {
        this.videoResultPath = videoResultPath;
    }
}
