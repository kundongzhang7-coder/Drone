-- =====================================================
-- 无人机识别系统 - MySQL 建库建表脚本
-- 首次切换到 MySQL 时，先在 MySQL 中执行本脚本
-- =====================================================

CREATE DATABASE IF NOT EXISTS drone_detection
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE drone_detection;

CREATE TABLE IF NOT EXISTS detection_task (
    id                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    video_name        VARCHAR(255) NULL COMMENT '视频名称',
    start_time        DATETIME     NULL COMMENT '检测开始时间',
    end_time          DATETIME     NULL COMMENT '检测结束时间',
    created_date      DATE         NULL COMMENT '任务创建日期（按天统计用）',
    video_result_path VARCHAR(512) NULL COMMENT '结果视频文件路径',
    PRIMARY KEY (id),
    KEY idx_created_date (created_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='检测任务表';
