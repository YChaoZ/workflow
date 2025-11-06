-- ========================================
-- 彻底修复数据库脚本
-- ========================================
-- 这个脚本会确保所有MVP2表被真正删除
-- ========================================

USE workflow;

-- 1. 先查看当前有哪些表
SELECT '=== 当前所有表 ===' AS '';
SHOW TABLES;

-- 2. 强制删除所有MVP2相关的表（忽略是否存在）
SELECT '=== 开始删除表 ===' AS '';

DROP TABLE IF EXISTS wf_task_attachment;
DROP TABLE IF EXISTS wf_task_comment;
DROP TABLE IF EXISTS wf_process_category;

SELECT '✅ MVP2表已删除' AS '';

-- 3. 删除Flyway历史记录（让Flyway重新执行迁移）
SELECT '=== 清理Flyway历史 ===' AS '';

DELETE FROM flyway_schema_history WHERE version IN ('3', '4');

SELECT '✅ Flyway历史已清理' AS '';

-- 4. 验证删除结果
SELECT '=== 验证删除结果 ===' AS '';
SHOW TABLES;

SELECT '✅ 数据库已彻底修复，现在可以重新启动服务' AS '';

