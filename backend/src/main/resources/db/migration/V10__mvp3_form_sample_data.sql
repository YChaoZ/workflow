-- ============================================
-- MVP3 表单示例数据
-- 创建时间: 2025-11-06
-- ============================================

-- 初始化示例表单定义（在V9添加列后插入）
INSERT IGNORE INTO wf_form_definition (form_key, form_name, form_type, form_config, version, category_id, description, status, create_by) VALUES
('leave_request', '请假申请表', 'CUSTOM', '{"fields":[{"type":"input","field":"applicant","title":"申请人","value":"","props":{"placeholder":"请输入申请人"}},{"type":"date","field":"startDate","title":"开始日期","value":"","props":{"placeholder":"请选择开始日期"}},{"type":"date","field":"endDate","title":"结束日期","value":"","props":{"placeholder":"请选择结束日期"}},{"type":"input-number","field":"days","title":"请假天数","value":1,"props":{"min":0.5,"step":0.5}},{"type":"select","field":"leaveType","title":"请假类型","value":"","options":[{"label":"事假","value":"personal"},{"label":"病假","value":"sick"},{"label":"年假","value":"annual"}]},{"type":"textarea","field":"reason","title":"请假事由","value":"","props":{"rows":4,"placeholder":"请输入请假事由"}}]}', 1, 2, '员工请假申请表单', 1, 'system'),
('expense_claim', '费用报销单', 'CUSTOM', '{"fields":[{"type":"input","field":"claimant","title":"报销人","value":"","props":{"placeholder":"请输入报销人"}},{"type":"date","field":"claimDate","title":"报销日期","value":"","props":{"placeholder":"请选择报销日期"}},{"type":"select","field":"expenseType","title":"费用类型","value":"","options":[{"label":"差旅费","value":"travel"},{"label":"交通费","value":"transport"},{"label":"招待费","value":"entertainment"},{"label":"办公费","value":"office"}]},{"type":"input-number","field":"amount","title":"报销金额","value":0,"props":{"min":0,"precision":2}},{"type":"textarea","field":"description","title":"费用说明","value":"","props":{"rows":4,"placeholder":"请详细说明费用用途"}}]}', 1, 3, '费用报销申请表单', 1, 'system');

