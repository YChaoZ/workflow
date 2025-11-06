-- 清理Flyway失败的迁移记录

USE workflow;

-- 1. 查看当前Flyway历史（执行前）
SELECT version, description, installed_on, execution_time, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC;

-- 2. 删除失败的V3和V4迁移记录
DELETE FROM flyway_schema_history WHERE version = '3' AND success = 0;
DELETE FROM flyway_schema_history WHERE version = '4' AND success = 0;

-- 3. 确认删除成功（执行后）
SELECT version, description, installed_on, execution_time, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC;

-- 预期结果：只应该看到V1和V2，且success都是1

