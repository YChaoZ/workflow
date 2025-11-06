-- ========================================
-- 工作流系统数据库重置脚本
-- ========================================

-- 删除并重新创建数据库
DROP DATABASE IF EXISTS workflow;
CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 显示确认信息
SELECT '✅ 数据库已完全重置' AS Status;

