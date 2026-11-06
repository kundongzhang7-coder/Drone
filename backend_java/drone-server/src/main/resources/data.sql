-- 初始化种子数据（幂等，可重复执行）
-- 默认账号：admin / admin123，demo / admin123（BCrypt 已加盐）

INSERT INTO sys_user (username, password, nickname, status)
SELECT 'admin', '$2a$10$qeYGZLa5nlm7R9NEPmJBee83AXRbCdBInmsOf1KYcTEYYAUc9b54y', '系统管理员', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user (username, password, nickname, status)
SELECT 'demo', '$2a$10$qeYGZLa5nlm7R9NEPmJBee83AXRbCdBInmsOf1KYcTEYYAUc9b54y', '演示用户', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo');
