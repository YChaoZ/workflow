-- ========================================
-- 检查表结构脚本
-- ========================================
-- 用于诊断表是否存在以及结构是否正确
-- ========================================

USE workflow;

-- 1. 查看所有表
SELECT '=== 所有表 ===' AS '';
SHOW TABLES;

-- 2. 检查wf_process_category表是否存在
SELECT '=== 检查 wf_process_category 表 ===' AS '';
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '❌ 表已存在（这是问题原因）'
        ELSE '✅ 表不存在（正常）'
    END AS 'wf_process_category状态'
FROM information_schema.tables 
WHERE table_schema = 'workflow' 
AND table_name = 'wf_process_category';

-- 3. 如果表存在，显示其结构
SELECT '=== wf_process_category 表结构 ===' AS '';
DESCRIBE wf_process_category;

-- 4. 查看Flyway历史
SELECT '=== Flyway历史记录 ===' AS '';
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;

