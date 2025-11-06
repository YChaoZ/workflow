-- 完全重置MVP2表并重新创建

USE workflow;

-- 1. 删除旧表（如果存在）
DROP TABLE IF EXISTS wf_task_attachment;
DROP TABLE IF EXISTS wf_task_comment;
DROP TABLE IF EXISTS wf_process_category;

-- 2. 删除失败的Flyway记录
DELETE FROM flyway_schema_history WHERE version IN ('3', '4');

-- 3. 验证清理结果
SELECT 'Flyway历史记录:' as info;
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;

SELECT '现有的wf_表:' as info;
SHOW TABLES LIKE 'wf%';

