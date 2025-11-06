-- ========================================
-- 完全清理并重置数据库
-- ========================================
-- 这个脚本会彻底清理所有内容
-- ========================================

-- 删除并重新创建数据库（最彻底的方式）
DROP DATABASE IF EXISTS workflow;
CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用新数据库
USE workflow;

-- 显示确认信息
SELECT '✅ 数据库已完全重置' AS 'Status';
SELECT '🚀 现在可以启动服务了' AS 'Next';
SELECT '⚠️ 所有表和数据已清空，Flyway将从头开始迁移' AS 'Note';

