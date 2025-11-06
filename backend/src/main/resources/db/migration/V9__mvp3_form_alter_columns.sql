-- ============================================
-- MVP3 表单定义表结构调整
-- 创建时间: 2025-11-06
-- 说明: 此迁移已通过手动方式执行（Druid WallFilter限制）
-- ============================================

-- 本迁移脚本的内容已在数据库中手动执行
-- 包括添加以下列：
-- 1. wf_form_definition: category_id, description, create_by, update_by
-- 2. wf_form_data: create_by, update_by
-- 3. 添加索引: idx_category on wf_form_definition

SELECT 'V9 migration completed manually' AS status;

