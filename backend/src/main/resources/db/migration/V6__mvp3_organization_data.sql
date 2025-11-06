-- ========================================
-- MVP3阶段：组织架构初始化数据
-- 版本：V6
-- 说明：插入权限、用户组初始数据
-- 注意：部门和角色数据在V2中已插入，这里只补充新增的权限和用户组
-- ========================================

-- 1. 插入技术部子部门（V2中只插入了一级部门）
INSERT INTO sys_department (dept_code, dept_name, parent_id, dept_level, dept_path, sort_order, status) 
SELECT 'IT_DEV', '开发组', id, 3, '/ROOT/IT/DEV', 1, 1 FROM sys_department WHERE dept_code='IT'
UNION ALL
SELECT 'IT_TEST', '测试组', id, 3, '/ROOT/IT/TEST', 2, 1 FROM sys_department WHERE dept_code='IT'
UNION ALL
SELECT 'IT_OPS', '运维组', id, 3, '/ROOT/IT/OPS', 3, 1 FROM sys_department WHERE dept_code='IT';

-- 2. 插入新的系统角色（V2中已有ADMIN、MANAGER、EMPLOYEE、VIEWER）
-- 注意：V1的sys_role表没有sort_order字段
INSERT INTO sys_role (role_code, role_name, role_type, description, status) VALUES
('SUPER_ADMIN', '超级管理员', 'SYSTEM', '拥有系统所有权限', 1),
('DEPT_MANAGER', '部门经理', 'CUSTOM', '部门管理权限', 1),
('WORKFLOW_ADMIN', '流程管理员', 'CUSTOM', '流程管理权限', 1);

-- 3. 插入权限（菜单和功能权限）
-- 一级菜单
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, icon, sort_order, status) VALUES
-- 系统管理
('SYSTEM', '系统管理', 'MENU', 0, '/system', 'el-icon-setting', 1, 1),
-- 组织管理
('ORG', '组织管理', 'MENU', 0, '/org', 'el-icon-user', 2, 1),
-- 流程管理
('WORKFLOW', '流程管理', 'MENU', 0, '/workflow', 'el-icon-document', 3, 1);

-- 系统管理子菜单
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, icon, sort_order, status)
SELECT 'SYSTEM_USER', '用户管理', 'MENU', id, '/system/user', 'el-icon-user', 1, 1 FROM sys_permission WHERE permission_code='SYSTEM'
UNION ALL
SELECT 'SYSTEM_ROLE', '角色管理', 'MENU', id, '/system/role', 'el-icon-s-custom', 2, 1 FROM sys_permission WHERE permission_code='SYSTEM'
UNION ALL
SELECT 'SYSTEM_PERMISSION', '权限管理', 'MENU', id, '/system/permission', 'el-icon-lock', 3, 1 FROM sys_permission WHERE permission_code='SYSTEM';

-- 组织管理子菜单
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, icon, sort_order, status)
SELECT 'ORG_DEPT', '部门管理', 'MENU', id, '/org/dept', 'el-icon-office-building', 1, 1 FROM sys_permission WHERE permission_code='ORG'
UNION ALL
SELECT 'ORG_GROUP', '用户组管理', 'MENU', id, '/org/group', 'el-icon-s-management', 2, 1 FROM sys_permission WHERE permission_code='ORG';

-- 流程管理子菜单
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, icon, sort_order, status)
SELECT 'WORKFLOW_DESIGN', '流程设计', 'MENU', id, '/workflow/design', 'el-icon-edit', 1, 1 FROM sys_permission WHERE permission_code='WORKFLOW'
UNION ALL
SELECT 'WORKFLOW_DEFINITION', '流程定义', 'MENU', id, '/workflow/definition', 'el-icon-s-order', 2, 1 FROM sys_permission WHERE permission_code='WORKFLOW'
UNION ALL
SELECT 'WORKFLOW_INSTANCE', '流程实例', 'MENU', id, '/workflow/instance', 'el-icon-s-platform', 3, 1 FROM sys_permission WHERE permission_code='WORKFLOW'
UNION ALL
SELECT 'WORKFLOW_TASK', '任务管理', 'MENU', id, '/workflow/task', 'el-icon-s-check', 4, 1 FROM sys_permission WHERE permission_code='WORKFLOW'
UNION ALL
SELECT 'WORKFLOW_CATEGORY', '流程分类', 'MENU', id, '/workflow/category', 'el-icon-menu', 5, 1 FROM sys_permission WHERE permission_code='WORKFLOW';

-- 用户管理按钮权限
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, resource_method, sort_order, status)
SELECT 'SYSTEM_USER_ADD', '添加用户', 'BUTTON', id, '/api/users', 'POST', 1, 1 FROM sys_permission WHERE permission_code='SYSTEM_USER'
UNION ALL
SELECT 'SYSTEM_USER_EDIT', '编辑用户', 'BUTTON', id, '/api/users/*', 'PUT', 2, 1 FROM sys_permission WHERE permission_code='SYSTEM_USER'
UNION ALL
SELECT 'SYSTEM_USER_DELETE', '删除用户', 'BUTTON', id, '/api/users/*', 'DELETE', 3, 1 FROM sys_permission WHERE permission_code='SYSTEM_USER'
UNION ALL
SELECT 'SYSTEM_USER_VIEW', '查看用户', 'BUTTON', id, '/api/users/*', 'GET', 4, 1 FROM sys_permission WHERE permission_code='SYSTEM_USER';

-- 角色管理按钮权限
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, resource_method, sort_order, status)
SELECT 'SYSTEM_ROLE_ADD', '添加角色', 'BUTTON', id, '/api/roles', 'POST', 1, 1 FROM sys_permission WHERE permission_code='SYSTEM_ROLE'
UNION ALL
SELECT 'SYSTEM_ROLE_EDIT', '编辑角色', 'BUTTON', id, '/api/roles/*', 'PUT', 2, 1 FROM sys_permission WHERE permission_code='SYSTEM_ROLE'
UNION ALL
SELECT 'SYSTEM_ROLE_DELETE', '删除角色', 'BUTTON', id, '/api/roles/*', 'DELETE', 3, 1 FROM sys_permission WHERE permission_code='SYSTEM_ROLE'
UNION ALL
SELECT 'SYSTEM_ROLE_ASSIGN', '分配权限', 'BUTTON', id, '/api/roles/*/permissions', 'POST', 4, 1 FROM sys_permission WHERE permission_code='SYSTEM_ROLE';

-- 部门管理按钮权限
INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_path, resource_method, sort_order, status)
SELECT 'ORG_DEPT_ADD', '添加部门', 'BUTTON', id, '/api/departments', 'POST', 1, 1 FROM sys_permission WHERE permission_code='ORG_DEPT'
UNION ALL
SELECT 'ORG_DEPT_EDIT', '编辑部门', 'BUTTON', id, '/api/departments/*', 'PUT', 2, 1 FROM sys_permission WHERE permission_code='ORG_DEPT'
UNION ALL
SELECT 'ORG_DEPT_DELETE', '删除部门', 'BUTTON', id, '/api/departments/*', 'DELETE', 3, 1 FROM sys_permission WHERE permission_code='ORG_DEPT';

-- 4. 超级管理员角色关联所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN'),
    id
FROM sys_permission;

-- 5. 普通员工角色关联基础权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE role_code = 'EMPLOYEE'),
    id
FROM sys_permission 
WHERE permission_code IN ('WORKFLOW', 'WORKFLOW_TASK', 'WORKFLOW_INSTANCE');

-- 6. 创建默认用户组
INSERT INTO sys_user_group (group_code, group_name, description, status) VALUES
('MANAGER_GROUP', '管理者组', '包含所有管理人员', 1),
('FINANCE_APPROVER', '财务审批组', '财务审批人员', 1),
('HR_APPROVER', '人事审批组', '人事审批人员', 1);

-- 7. 更新admin用户关联部门（假设admin是总公司成员）
UPDATE sys_user SET dept_id = 1 WHERE username = 'admin';

