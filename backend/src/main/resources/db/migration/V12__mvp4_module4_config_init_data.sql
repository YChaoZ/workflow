-- ================================================================
-- MVP4 模块4：系统配置管理 - 初始化数据
-- 创建时间：2025-11-06
-- 说明：初始化系统参数和数据字典数据
-- ================================================================

-- ==================== 系统参数初始化 ====================

-- 工作流配置
INSERT INTO sys_param (param_key, param_value, param_name, param_type, param_group, description, is_system, sort_order) VALUES
('workflow.timeout.default', '3600', '默认超时时间(秒)', 'NUMBER', 'workflow', '流程任务默认超时时间，单位：秒', 1, 1),
('workflow.auto.complete.enabled', 'false', '自动完成开关', 'BOOLEAN', 'workflow', '是否启用任务自动完成功能', 1, 2),
('workflow.history.retain.days', '90', '历史数据保留天数', 'NUMBER', 'workflow', '已完成流程的历史数据保留天数', 1, 3),
('workflow.max.instance.per.user', '100', '用户最大流程实例数', 'NUMBER', 'workflow', '单个用户同时可发起的最大流程实例数', 1, 4);

-- 文件上传配置
INSERT INTO sys_param (param_key, param_value, param_name, param_type, param_group, description, is_system, sort_order) VALUES
('upload.max.size', '10485760', '文件上传大小限制(字节)', 'NUMBER', 'upload', '单个文件最大上传大小，默认10MB', 1, 1),
('upload.allowed.types', 'jpg,jpeg,png,pdf,doc,docx,xls,xlsx,zip,rar', '允许上传的文件类型', 'STRING', 'upload', '允许上传的文件扩展名，逗号分隔', 1, 2),
('upload.path.base', '/data/uploads', '文件存储基础路径', 'STRING', 'upload', '上传文件的存储基础路径', 1, 3);

-- 系统配置
INSERT INTO sys_param (param_key, param_value, param_name, param_type, param_group, description, is_system, sort_order) VALUES
('system.page.size.default', '10', '默认分页大小', 'NUMBER', 'system', '列表查询默认每页显示数量', 1, 1),
('system.cache.enabled', 'true', '缓存开关', 'BOOLEAN', 'system', '是否启用Redis缓存', 1, 2),
('system.cache.expire.seconds', '3600', '缓存过期时间(秒)', 'NUMBER', 'system', 'Redis缓存默认过期时间', 1, 3),
('system.session.timeout', '1800', '会话超时时间(秒)', 'NUMBER', 'system', '用户会话超时时间，默认30分钟', 1, 4);

-- ==================== 数据字典初始化 ====================

-- 字典类型
INSERT INTO sys_dict_type (dict_code, dict_name, description, is_system, status, sort_order) VALUES
('task_priority', '任务优先级', '任务的优先级级别', 1, 1, 1),
('process_status', '流程状态', '流程实例的运行状态', 1, 1, 2),
('user_status', '用户状态', '用户账号的状态', 1, 1, 3),
('approval_result', '审批结果', '任务审批的结果类型', 1, 1, 4),
('task_status', '任务状态', '任务的状态', 1, 1, 5);

-- 字典数据 - 任务优先级
INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, tag_type, sort_order, status) VALUES
(1, 'task_priority', '高', 'HIGH', 'danger', 1, 1),
(1, 'task_priority', '中', 'MEDIUM', 'warning', 2, 1),
(1, 'task_priority', '低', 'LOW', 'info', 3, 1);

-- 字典数据 - 流程状态
INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, tag_type, sort_order, status) VALUES
(2, 'process_status', '运行中', 'RUNNING', 'primary', 1, 1),
(2, 'process_status', '已完成', 'COMPLETED', 'success', 2, 1),
(2, 'process_status', '已终止', 'TERMINATED', 'danger', 3, 1),
(2, 'process_status', '已挂起', 'SUSPENDED', 'warning', 4, 1);

-- 字典数据 - 用户状态
INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, tag_type, sort_order, status) VALUES
(3, 'user_status', '正常', 'NORMAL', 'success', 1, 1),
(3, 'user_status', '锁定', 'LOCKED', 'warning', 2, 1),
(3, 'user_status', '离职', 'RESIGNED', 'info', 3, 1);

-- 字典数据 - 审批结果
INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, tag_type, sort_order, status) VALUES
(4, 'approval_result', '同意', 'APPROVED', 'success', 1, 1),
(4, 'approval_result', '拒绝', 'REJECTED', 'danger', 2, 1),
(4, 'approval_result', '退回', 'RETURNED', 'warning', 3, 1);

-- 字典数据 - 任务状态
INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, tag_type, sort_order, status) VALUES
(5, 'task_status', '待办', 'TODO', 'primary', 1, 1),
(5, 'task_status', '已完成', 'DONE', 'success', 2, 1),
(5, 'task_status', '已取消', 'CANCELLED', 'info', 3, 1);

