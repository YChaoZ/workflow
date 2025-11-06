-- ================================================================
-- MVP4 模块4：系统配置管理 - 表结构
-- 创建时间：2025-11-06
-- 说明：包含系统参数表、数据字典类型表、数据字典数据表
-- ================================================================

-- 1. 系统参数表
CREATE TABLE IF NOT EXISTS sys_param (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID',
    param_key VARCHAR(100) NOT NULL UNIQUE COMMENT '参数键',
    param_value VARCHAR(500) COMMENT '参数值',
    param_name VARCHAR(100) NOT NULL COMMENT '参数名称',
    param_type VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT '参数类型: STRING/NUMBER/BOOLEAN/JSON',
    param_group VARCHAR(50) COMMENT '参数分组',
    description VARCHAR(500) COMMENT '参数描述',
    is_system TINYINT(1) DEFAULT 0 COMMENT '是否系统参数（不可删除）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_param_group (param_group),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- 2. 数据字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典类型ID',
    dict_code VARCHAR(100) NOT NULL UNIQUE COMMENT '字典编码',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    description VARCHAR(500) COMMENT '描述',
    is_system TINYINT(1) DEFAULT 0 COMMENT '是否系统字典（不可删除）',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_status (status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典类型表';

-- 3. 数据字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典数据ID',
    dict_type_id BIGINT NOT NULL COMMENT '字典类型ID',
    dict_code VARCHAR(100) NOT NULL COMMENT '字典类型编码（冗余）',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典值',
    tag_type VARCHAR(20) COMMENT '标签类型：primary/success/info/warning/danger',
    css_class VARCHAR(100) COMMENT 'CSS类名',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认值',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_dict_type (dict_type_id, dict_code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order),
    CONSTRAINT fk_dict_data_type FOREIGN KEY (dict_type_id) 
        REFERENCES sys_dict_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典数据表';

