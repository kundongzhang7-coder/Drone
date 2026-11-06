package com.drone.service;

/**
 * Python 检测服务返回结果
 */
public record PythonDetectionResult(
        String resultVideoUrl,
        Integer totalFrames,
        Integer droneCount
) {
}
