package com.drone.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DetectionStatsVO implements Serializable {

    private Long totalTasks;
    private Long completedTasks;
    private Long processingTasks;
    private Long totalDrones;
    private Long totalFrames;
}
