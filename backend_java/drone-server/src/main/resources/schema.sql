-- 无人机智能识别系统建表脚本（幂等，可重复执行）

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    nickname    VARCHAR(50)  COMMENT '昵称',
    avatar      VARCHAR(500) COMMENT '头像 COS URL',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20)  COMMENT '手机号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常，0 禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

CREATE TABLE IF NOT EXISTS detection_task (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_name        VARCHAR(100) NOT NULL COMMENT '任务名称',
    user_id          BIGINT       COMMENT '归属用户 ID',
    status           VARCHAR(20)  NOT NULL COMMENT '状态：PROCESSING/COMPLETED/FAILED',
    start_time       DATETIME     COMMENT '开始时间',
    end_time         DATETIME     COMMENT '结束时间',
    total_frames     INT          DEFAULT 0 COMMENT '处理帧数',
    drone_count      INT          DEFAULT 0 COMMENT '识别无人机数',
    result_video_url VARCHAR(500) COMMENT '结果视频 COS URL',
    source_type      VARCHAR(20)  DEFAULT 'REALTIME' COMMENT '来源 REALTIME/VIDEO',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '检测任务表';
