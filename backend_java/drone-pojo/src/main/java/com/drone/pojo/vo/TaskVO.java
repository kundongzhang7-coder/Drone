package com.drone.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TaskVO implements Serializable {

    private Long id;
    private String taskName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalFrames;
    private Integer droneCount;
    private String resultVideoUrl;
    private String sourceType;
}
