package com.drone.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 无人机检测任务实体
 */
@Data
public class DetectionTask implements Serializable {

    private Long id;
    private String taskName;
    /** 任务归属用户 */
    private Long userId;
    /** 状态：PROCESSING / COMPLETED / FAILED */
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalFrames;
    private Integer droneCount;
    /** 结果视频在 COS 上的访问 URL（数据库只存 URL，不存文件） */
    private String resultVideoUrl;
    /** 来源：REALTIME 实时检测 / VIDEO 离线视频检测 */
    private String sourceType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
