-- ============================================================
-- 批量灌入检测任务数据，用于缓存击穿/穿透/雪崩与高并发压测
-- 用法（容器内）：
--   docker exec -i drone-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" drone_detection < seed_tasks.sql
-- 或进入 mysql 客户端后执行本文件。
-- ============================================================
USE drone_detection;

DROP PROCEDURE IF EXISTS seed_tasks;

DELIMITER $$
CREATE PROCEDURE seed_tasks(IN total INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE v_user BIGINT;
    DECLARE v_status VARCHAR(20);
    DECLARE v_source VARCHAR(20);
    DECLARE v_start DATETIME;
    DECLARE v_end DATETIME;

    WHILE i < total DO
        SET v_user   = 1 + FLOOR(RAND() * 2);
        SET v_status = ELT(1 + FLOOR(RAND() * 3), 'COMPLETED', 'PROCESSING', 'FAILED');
        SET v_source = ELT(1 + FLOOR(RAND() * 2), 'REALTIME', 'VIDEO');
        SET v_start  = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY);
        SET v_end    = IF(v_status = 'COMPLETED',
                          DATE_ADD(v_start, INTERVAL FLOOR(1 + RAND() * 60) MINUTE), NULL);

        INSERT INTO detection_task
            (task_name, user_id, status, start_time, end_time,
             total_frames, drone_count, source_type, create_time, update_time)
        VALUES
            (CONCAT('压测任务-', i), v_user, v_status, v_start, v_end,
             FLOOR(100 + RAND() * 5000), FLOOR(RAND() * 10),
             v_source, v_start, v_start);

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

-- 默认灌入 10 万行，可按需调整参数
CALL seed_tasks(100000);

DROP PROCEDURE seed_tasks;
