-- ============================================
-- MVP3 表单设计器相关表
-- 创建时间: 2025-11-06
-- ============================================

-- 1. 表单定义表
CREATE TABLE IF NOT EXISTS wf_form_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '表单ID',
    form_key VARCHAR(100) NOT NULL COMMENT '表单Key（唯一标识）',
    form_name VARCHAR(200) NOT NULL COMMENT '表单名称',
    form_type VARCHAR(50) NOT NULL COMMENT '表单类型（CUSTOM:自定义, BUILT_IN:内置）',
    form_config TEXT COMMENT '表单配置JSON（包含字段、布局、验证规则等）',
    version INT DEFAULT 1 COMMENT '版本号',
    category_id BIGINT COMMENT '表单分类ID',
    description VARCHAR(500) COMMENT '表单描述',
    status TINYINT DEFAULT 1 COMMENT '状态（0:禁用 1:启用 2:草稿）',
    create_by VARCHAR(100) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(100) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除 1:已删除）',
    UNIQUE KEY uk_form_key_version (form_key, version, deleted),
    KEY idx_category (category_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单定义表';

-- 2. 表单数据表
CREATE TABLE IF NOT EXISTS wf_form_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据ID',
    form_key VARCHAR(100) NOT NULL COMMENT '表单Key',
    form_version INT DEFAULT 1 COMMENT '表单版本',
    business_key VARCHAR(100) COMMENT '业务主键（如流程实例ID）',
    form_data TEXT COMMENT '表单数据JSON',
    create_by VARCHAR(100) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(100) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_form_key (form_key),
    KEY idx_business_key (business_key),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单数据表';

-- 3. 流程表单关联表
CREATE TABLE IF NOT EXISTS wf_process_form (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    process_definition_key VARCHAR(100) NOT NULL COMMENT '流程定义Key',
    node_id VARCHAR(100) COMMENT '节点ID（为空表示全局表单）',
    form_key VARCHAR(100) NOT NULL COMMENT '表单Key',
    form_version INT DEFAULT 1 COMMENT '表单版本',
    is_required TINYINT DEFAULT 1 COMMENT '是否必填（0:否 1:是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_process_node_form (process_definition_key, node_id, form_key),
    KEY idx_form_key (form_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程表单关联表';

-- 4. 表单分类表
CREATE TABLE IF NOT EXISTS wf_form_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除 1:已删除）',
    UNIQUE KEY uk_code (category_code, deleted),
    KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单分类表';

-- 5. 动态审批人配置表
CREATE TABLE IF NOT EXISTS wf_dynamic_assignee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    process_definition_key VARCHAR(100) NOT NULL COMMENT '流程定义Key',
    node_id VARCHAR(100) NOT NULL COMMENT '节点ID',
    assignee_type VARCHAR(50) NOT NULL COMMENT '审批人类型（USER:用户, ROLE:角色, DEPT:部门, DEPT_MANAGER:部门经理, INITIATOR:发起人, FORM_FIELD:表单字段）',
    assignee_value VARCHAR(500) COMMENT '审批人值（用户ID/角色ID/部门ID/表单字段名等）',
    assignee_rule TEXT COMMENT '审批人规则JSON（复杂规则配置）',
    priority INT DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_process_node (process_definition_key, node_id),
    KEY idx_assignee_type (assignee_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态审批人配置表';

