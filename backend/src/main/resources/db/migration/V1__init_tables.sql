-- ========================================
-- 工作流系统数据库初始化脚本
-- 版本：V1
-- 说明：创建基础表结构（Flowable表由引擎自动创建）
-- ========================================

-- 流程分类表
CREATE TABLE IF NOT EXISTS wf_process_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程分类表';

-- 流程定义扩展表
CREATE TABLE IF NOT EXISTS wf_process_definition_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    process_definition_id VARCHAR(64) NOT NULL COMMENT 'Flowable流程定义ID',
    process_key VARCHAR(50) NOT NULL COMMENT '流程KEY',
    process_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    category_id BIGINT COMMENT '分类ID',
    version INT DEFAULT 1 COMMENT '版本号',
    description TEXT COMMENT '流程描述',
    icon VARCHAR(200) COMMENT '图标',
    form_id BIGINT COMMENT '关联表单ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_user VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_process_key (process_key),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义扩展表';

-- 流程实例扩展表
CREATE TABLE IF NOT EXISTS wf_process_instance_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    process_instance_id VARCHAR(64) NOT NULL COMMENT 'Flowable流程实例ID',
    business_key VARCHAR(100) COMMENT '业务KEY',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id VARCHAR(100) COMMENT '业务ID',
    title VARCHAR(200) COMMENT '流程标题',
    priority INT DEFAULT 0 COMMENT '优先级',
    start_user VARCHAR(50) COMMENT '发起人',
    start_dept VARCHAR(100) COMMENT '发起部门',
    current_node VARCHAR(100) COMMENT '当前节点',
    status VARCHAR(20) COMMENT '状态：RUNNING,SUSPENDED,COMPLETED,TERMINATED',
    result VARCHAR(20) COMMENT '结果：APPROVED,REJECTED',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    end_time DATETIME COMMENT '结束时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_instance_id (process_instance_id),
    KEY idx_business_key (business_key),
    KEY idx_start_user (start_user),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例扩展表';

-- 表单定义表
CREATE TABLE IF NOT EXISTS wf_form_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    form_key VARCHAR(50) NOT NULL COMMENT '表单KEY',
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_type VARCHAR(20) DEFAULT 'CUSTOM' COMMENT '表单类型：CUSTOM-自定义,EXTERNAL-外部',
    form_config JSON COMMENT '表单配置（JSON）',
    form_schema JSON COMMENT '表单Schema',
    version INT DEFAULT 1 COMMENT '版本号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark TEXT COMMENT '备注',
    create_user VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_form_key (form_key, deleted),
    KEY idx_form_name (form_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单定义表';

-- 表单数据表
CREATE TABLE IF NOT EXISTS wf_form_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    form_id BIGINT NOT NULL COMMENT '表单ID',
    process_instance_id VARCHAR(64) COMMENT '流程实例ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    form_data JSON NOT NULL COMMENT '表单数据（JSON）',
    submit_user VARCHAR(50) COMMENT '提交人',
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_form_id (form_id),
    KEY idx_instance_id (process_instance_id),
    KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单数据表';

-- 任务扩展表
CREATE TABLE IF NOT EXISTS wf_task_ext (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    task_id VARCHAR(64) NOT NULL COMMENT 'Flowable任务ID',
    process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    task_name VARCHAR(100) COMMENT '任务名称',
    assignee VARCHAR(50) COMMENT '办理人',
    owner VARCHAR(50) COMMENT '任务拥有者',
    candidate_users TEXT COMMENT '候选用户（逗号分隔）',
    candidate_groups TEXT COMMENT '候选组（逗号分隔）',
    due_date DATETIME COMMENT '截止时间',
    follow_up_date DATETIME COMMENT '跟进时间',
    priority INT DEFAULT 0 COMMENT '优先级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    claim_time DATETIME COMMENT '签收时间',
    complete_time DATETIME COMMENT '完成时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_task_id (task_id),
    KEY idx_assignee (assignee),
    KEY idx_instance_id (process_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务扩展表';

-- 任务意见表
CREATE TABLE IF NOT EXISTS wf_task_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
    process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    comment_type VARCHAR(20) DEFAULT 'COMMENT' COMMENT '意见类型：COMMENT-批注,APPROVE-同意,REJECT-拒绝',
    comment_text TEXT COMMENT '意见内容',
    attachments TEXT COMMENT '附件（JSON数组）',
    user_id VARCHAR(50) COMMENT '用户ID',
    user_name VARCHAR(100) COMMENT '用户名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_task_id (task_id),
    KEY idx_instance_id (process_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务意见表';

-- 抄送表
CREATE TABLE IF NOT EXISTS wf_task_copy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    title VARCHAR(200) COMMENT '标题',
    copy_user VARCHAR(50) NOT NULL COMMENT '抄送人',
    copy_reason VARCHAR(200) COMMENT '抄送原因',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_copy_user (copy_user),
    KEY idx_instance_id (process_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抄送表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    dept_code VARCHAR(50) NOT NULL COMMENT '部门编码',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_level INT DEFAULT 1 COMMENT '部门层级',
    dept_path VARCHAR(500) COMMENT '部门路径',
    manager_id BIGINT COMMENT '负责人ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_dept_code (dept_code, deleted),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    real_name VARCHAR(100) COMMENT '真实姓名',
    password VARCHAR(100) COMMENT '密码',
    dept_id BIGINT COMMENT '部门ID',
    position VARCHAR(100) COMMENT '职位',
    email VARCHAR(100) COMMENT '邮箱',
    mobile VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_username (username, deleted),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_type VARCHAR(20) DEFAULT 'CUSTOM' COMMENT '角色类型',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_role_code (role_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_role (user_id, role_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关系表';

-- 流程监控记录表
CREATE TABLE IF NOT EXISTS wf_monitor_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    node_key VARCHAR(100) COMMENT '节点KEY',
    node_name VARCHAR(100) COMMENT '节点名称',
    event_type VARCHAR(20) COMMENT '事件类型：START,END,TIMEOUT',
    duration BIGINT COMMENT '持续时间（毫秒）',
    record_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_instance_id (process_instance_id),
    KEY idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程监控记录表';

-- 创建数据库索引优化（针对Flowable表）
-- 注意：这些表由Flowable自动创建，这里只是添加额外的索引优化
-- 实际使用时需要等Flowable表创建完成后手动执行

-- ALTER TABLE ACT_RU_TASK ADD INDEX idx_assignee_status (ASSIGNEE_, SUSPENSION_STATE_);
-- ALTER TABLE ACT_HI_TASKINST ADD INDEX idx_assignee_end (ASSIGNEE_, END_TIME_);
-- ALTER TABLE ACT_HI_PROCINST ADD INDEX idx_start_end_time (START_TIME_, END_TIME_);

