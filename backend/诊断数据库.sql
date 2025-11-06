-- 数据库诊断脚本
-- 请在MySQL客户端执行

USE workflow;

-- 1. 查看所有表
SHOW TABLES;

-- 2. 查看Flyway历史
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- 3. 如果wf_process_category表存在，查看结构
DESCRIBE wf_process_category;

-- 4. 查看表创建语句
SHOW CREATE TABLE wf_process_category;

