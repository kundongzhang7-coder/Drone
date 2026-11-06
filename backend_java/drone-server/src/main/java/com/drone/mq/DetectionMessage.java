package com.drone.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 离线检测任务消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionMessage implements Serializable {

    private Long taskId;
    private Long userId;
    /** 待检测视频的 COS URL 或可访问地址 */
    private String videoUrl;
    private String requestId;
}
