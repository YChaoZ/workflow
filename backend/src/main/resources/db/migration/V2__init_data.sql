-- ========================================
-- 工作流系统初始化数据脚本
-- 版本：V2
-- 说明：插入初始化数据
-- ========================================

-- 插入流程分类
INSERT INTO wf_process_category (category_code, category_name, parent_id, sort_order) VALUES
('APPROVAL', '审批流程', 0, 1),
('FINANCE', '财务流程', 0, 2),
('HR', '人事流程', 0, 3),
('OA', '办公流程', 0, 4);

-- 插入部门
INSERT INTO sys_department (dept_code, dept_name, parent_id, dept_level, dept_path, sort_order, status) VALUES
('ROOT', '总公司', 0, 1, '/ROOT', 1, 1),
('IT', '技术部', 1, 2, '/ROOT/IT', 1, 1),
('HR', '人力资源部', 1, 2, '/ROOT/HR', 2, 1),
('FINANCE', '财务部', 1, 2, '/ROOT/FINANCE', 3, 1),
('ADMIN', '行政部', 1, 2, '/ROOT/ADMIN', 4, 1);

-- 插入角色
INSERT INTO sys_role (role_code, role_name, role_type, description, status) VALUES
('ADMIN', '系统管理员', 'SYSTEM', '系统管理员，拥有所有权限', 1),
('MANAGER', '部门经理', 'CUSTOM', '部门经理，管理本部门流程', 1),
('EMPLOYEE', '普通员工', 'CUSTOM', '普通员工，可以发起和处理流程', 1),
('VIEWER', '查看者', 'CUSTOM', '查看者，只能查看流程', 1);

-- 插入默认管理员用户，密码统一为 admin123（BCrypt加密）
INSERT INTO sys_user (username, real_name, password, dept_id, position, email, mobile, status) VALUES
('admin', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '系统管理员', 'admin@workflow.com', '13800138000', 1),
('zhangsan', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 2, '开发工程师', 'zhangsan@workflow.com', '13800138001', 1),
('lisi', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 2, '技术经理', 'lisi@workflow.com', '13800138002', 1);

-- 关联用户和角色
-- (1,1)=admin为系统管理员, (2,3)=zhangsan为普通员工, (3,2)=lisi为部门经理
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 2);

-- 插入示例表单定义
INSERT INTO wf_form_definition (form_key, form_name, form_type, form_config, version, status, create_user) VALUES
('LEAVE_FORM', '请假申请表', 'CUSTOM', 
'{"fields": [{"type": "date", "label": "开始日期", "name": "startDate", "required": true}, {"type": "date", "label": "结束日期", "name": "endDate", "required": true}, {"type": "number", "label": "请假天数", "name": "days", "required": true}, {"type": "textarea", "label": "请假原因", "name": "reason", "required": true}]}',
1, 1, 'admin');

-- 说明：Flowable 的表会在应用启动时自动创建
-- 说明：默认密码都是 admin123

