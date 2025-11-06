# MVP4 模块4 - 系统配置 设计方案

**创建日期：** 2025-11-06  
**模块名称：** 系统配置管理  
**开发状态：** 设计中  

---

## 📋 模块概述

系统配置模块提供灵活的配置管理能力，包括系统参数管理和数据字典管理，支持运行时动态配置调整。

---

## 🎯 功能需求

### 1. 系统参数管理

**功能描述：** 管理系统运行时的各种配置参数

**核心功能：**
- ✅ 参数分类管理（按模块分类）
- ✅ 参数CRUD操作
- ✅ 参数值类型支持（字符串、数字、布尔、JSON）
- ✅ 参数值验证
- ✅ 参数值缓存
- ✅ 参数修改历史记录

**应用场景：**
- 流程超时时间配置
- 邮件服务器配置
- 文件上传大小限制
- 系统开关配置
- 第三方API配置

---

### 2. 数据字典管理

**功能描述：** 管理系统中的各类枚举值和码表数据

**核心功能：**
- ✅ 字典分类管理（字典类型）
- ✅ 字典项CRUD操作
- ✅ 字典项排序
- ✅ 字典项启用/禁用
- ✅ 字典值缓存
- ✅ 多语言支持（预留）

**应用场景：**
- 任务优先级（高、中、低）
- 流程状态（运行中、已完成、已终止）
- 用户状态（正常、锁定、离职）
- 审批结果（同意、拒绝、退回）

---

## 📊 数据模型设计

### 1. 系统参数表 (sys_param)

```sql
CREATE TABLE sys_param (
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
```

**字段说明：**
- `param_key`: 参数键，全局唯一，建议使用点分隔命名（如：`workflow.timeout.default`）
- `param_type`: 支持 STRING、NUMBER、BOOLEAN、JSON 四种类型
- `param_group`: 参数分组，如：workflow、system、mail、upload
- `is_system`: 系统参数不可删除，只能修改值

---

### 2. 数据字典类型表 (sys_dict_type)

```sql
CREATE TABLE sys_dict_type (
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
```

---

### 3. 数据字典数据表 (sys_dict_data)

```sql
CREATE TABLE sys_dict_data (
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
```

**字段说明：**
- `dict_code`: 冗余字段，方便查询，避免总是JOIN
- `tag_type`: 用于前端显示，对应Element Plus的tag类型
- `is_default`: 标记默认值，可用于表单初始化

---

## 🏗️ 领域模型设计

### 1. 系统参数 (SystemParam)

```java
package com.bank.workflow.domain.config.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemParam {
    private Long id;
    private String paramKey;
    private String paramValue;
    private String paramName;
    private ParamType paramType;
    private String paramGroup;
    private String description;
    private Boolean isSystem;
    private Integer sortOrder;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
    private String remark;
    
    public enum ParamType {
        STRING, NUMBER, BOOLEAN, JSON
    }
    
    /**
     * 获取类型化的参数值
     */
    public Object getTypedValue() {
        if (paramValue == null) return null;
        
        return switch (paramType) {
            case NUMBER -> {
                try {
                    yield paramValue.contains(".") 
                        ? Double.parseDouble(paramValue) 
                        : Long.parseLong(paramValue);
                } catch (NumberFormatException e) {
                    yield paramValue;
                }
            }
            case BOOLEAN -> Boolean.parseBoolean(paramValue);
            case JSON -> paramValue; // 返回JSON字符串，由调用方解析
            default -> paramValue;
        };
    }
}
```

---

### 2. 数据字典类型 (DictType)

```java
package com.bank.workflow.domain.config.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DictType {
    private Long id;
    private String dictCode;
    private String dictName;
    private String description;
    private Boolean isSystem;
    private Boolean status;
    private Integer sortOrder;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
    private String remark;
    
    // 关联的字典数据
    private List<DictData> dictDataList;
}
```

---

### 3. 数据字典数据 (DictData)

```java
package com.bank.workflow.domain.config.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictData {
    private Long id;
    private Long dictTypeId;
    private String dictCode;
    private String dictLabel;
    private String dictValue;
    private String tagType;
    private String cssClass;
    private Boolean isDefault;
    private Boolean status;
    private Integer sortOrder;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
    private String remark;
}
```

---

## 🔌 API设计

### 系统参数管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/config/params` | 查询参数列表 |
| GET | `/api/config/params/{id}` | 查询参数详情 |
| GET | `/api/config/params/key/{key}` | 根据key查询参数 |
| GET | `/api/config/params/group/{group}` | 查询分组下的参数 |
| POST | `/api/config/params` | 创建参数 |
| PUT | `/api/config/params/{id}` | 更新参数 |
| DELETE | `/api/config/params/{id}` | 删除参数 |
| POST | `/api/config/params/refresh-cache` | 刷新参数缓存 |

---

### 数据字典管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/config/dict/types` | 查询字典类型列表 |
| GET | `/api/config/dict/types/{id}` | 查询字典类型详情 |
| POST | `/api/config/dict/types` | 创建字典类型 |
| PUT | `/api/config/dict/types/{id}` | 更新字典类型 |
| DELETE | `/api/config/dict/types/{id}` | 删除字典类型 |
| GET | `/api/config/dict/data/type/{typeCode}` | 根据类型编码查询字典数据 |
| GET | `/api/config/dict/data/{id}` | 查询字典数据详情 |
| POST | `/api/config/dict/data` | 创建字典数据 |
| PUT | `/api/config/dict/data/{id}` | 更新字典数据 |
| DELETE | `/api/config/dict/data/{id}` | 删除字典数据 |
| POST | `/api/config/dict/refresh-cache` | 刷新字典缓存 |

---

## 💾 缓存策略

### 1. 系统参数缓存

**缓存Key：** `sys:param:{paramKey}`  
**过期时间：** 1小时  
**刷新策略：** 修改参数时主动刷新

```java
// 伪代码
@Cacheable(value = "sys:param", key = "#paramKey")
public SystemParam getParamByKey(String paramKey) {
    return paramMapper.selectByKey(paramKey);
}

@CacheEvict(value = "sys:param", key = "#param.paramKey")
public void updateParam(SystemParam param) {
    paramMapper.updateById(param);
}
```

---

### 2. 数据字典缓存

**缓存Key：** `sys:dict:{dictCode}`  
**过期时间：** 1小时  
**刷新策略：** 修改字典时主动刷新

```java
// 伪代码
@Cacheable(value = "sys:dict", key = "#dictCode")
public List<DictData> getDictDataByType(String dictCode) {
    return dictDataMapper.selectByTypeCode(dictCode);
}

@CacheEvict(value = "sys:dict", key = "#dictCode")
public void updateDictData(String dictCode, DictData data) {
    dictDataMapper.updateById(data);
}
```

---

## 🎨 前端页面设计

### 1. 系统参数管理页面

**路由：** `/config/params`

**功能布局：**
```
┌─────────────────────────────────────────────┐
│ 🔍 搜索栏                                     │
│ [参数名称] [参数分组] [查询] [重置] [+ 新增]   │
├─────────────────────────────────────────────┤
│ 📋 参数列表表格                                │
│ ┌──────────────────────────────────────────┐│
│ │参数键 | 参数名称 | 参数值 | 分组 | 操作    ││
│ ├──────────────────────────────────────────┤│
│ │workflow.timeout | 超时时间 | 3600 | ... ││
│ └──────────────────────────────────────────┘│
├─────────────────────────────────────────────┤
│ 分页                                          │
└─────────────────────────────────────────────┘
```

**操作按钮：**
- 查看
- 编辑
- 删除（系统参数不可删除）
- 刷新缓存

---

### 2. 数据字典管理页面

**路由：** `/config/dict`

**功能布局（左右分栏）：**
```
┌──────────┬───────────────────────────────┐
│ 字典类型  │ 字典数据                        │
│          │                               │
│ □ 任务优先│ 🔍 搜索栏 [+ 新增]            │
│ □ 流程状态│ ┌─────────────────────────┐  │
│ □ 用户状态│ │标签 | 值 | 状态 | 操作   │  │
│ [+ 新增] │ ├─────────────────────────┤  │
│          │ │高   | HIGH | 启用 | ...  │  │
│          │ │中   | MEDIUM | 启用 |... │  │
│          │ └─────────────────────────┘  │
└──────────┴───────────────────────────────┘
```

**操作按钮：**
- 字典类型：新增、编辑、删除
- 字典数据：新增、编辑、删除、启用/禁用、排序

---

## 📦 初始化数据

### 系统参数初始数据

```sql
INSERT INTO sys_param (param_key, param_value, param_name, param_type, param_group, description, is_system) VALUES
-- 工作流配置
('workflow.timeout.default', '3600', '默认超时时间(秒)', 'NUMBER', 'workflow', '流程任务默认超时时间', 1),
('workflow.auto.complete.enabled', 'false', '自动完成开关', 'BOOLEAN', 'workflow', '是否启用任务自动完成', 1),
('workflow.history.retain.days', '90', '历史数据保留天数', 'NUMBER', 'workflow', '已完成流程的历史数据保留天数', 1),

-- 文件上传配置
('upload.max.size', '10485760', '文件上传大小限制(字节)', 'NUMBER', 'upload', '单个文件最大上传大小，默认10MB', 1),
('upload.allowed.types', 'jpg,jpeg,png,pdf,doc,docx,xls,xlsx', '允许上传的文件类型', 'STRING', 'upload', '允许上传的文件扩展名', 1),

-- 系统配置
('system.page.size.default', '10', '默认分页大小', 'NUMBER', 'system', '列表查询默认每页显示数量', 1),
('system.cache.enabled', 'true', '缓存开关', 'BOOLEAN', 'system', '是否启用Redis缓存', 1);
```

---

### 数据字典初始数据

```sql
-- 字典类型
INSERT INTO sys_dict_type (dict_code, dict_name, description, is_system, status) VALUES
('task_priority', '任务优先级', '任务的优先级级别', 1, 1),
('process_status', '流程状态', '流程实例的状态', 1, 1),
('user_status', '用户状态', '用户账号的状态', 1, 1),
('approval_result', '审批结果', '任务审批的结果', 1, 1);

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
```

---

## 🔐 权限设计

### 权限点

| 权限码 | 权限名称 | 说明 |
|--------|---------|------|
| `config:param:list` | 参数列表查询 | 查看参数列表 |
| `config:param:detail` | 参数详情查询 | 查看参数详情 |
| `config:param:create` | 参数创建 | 创建新参数 |
| `config:param:update` | 参数更新 | 修改参数值 |
| `config:param:delete` | 参数删除 | 删除参数 |
| `config:dict:list` | 字典列表查询 | 查看字典列表 |
| `config:dict:detail` | 字典详情查询 | 查看字典详情 |
| `config:dict:create` | 字典创建 | 创建新字典 |
| `config:dict:update` | 字典更新 | 修改字典数据 |
| `config:dict:delete` | 字典删除 | 删除字典 |

---

## 📝 开发计划

### 阶段1：后端开发（60分钟）

1. **数据库设计（10分钟）**
   - 创建Flyway迁移脚本
   - 初始化表结构和数据

2. **领域模型开发（15分钟）**
   - 创建实体类
   - 创建Gateway接口

3. **基础设施层开发（20分钟）**
   - 实现Mapper
   - 实现Gateway实现类
   - 配置缓存

4. **应用服务开发（15分钟）**
   - 实现AppService
   - 实现Controller
   - API测试

---

### 阶段2：前端开发（60分钟）

1. **API封装（10分钟）**
   - 创建TypeScript接口定义
   - 封装API调用方法

2. **参数管理页面（25分钟）**
   - 列表展示
   - 表单编辑
   - 缓存刷新

3. **字典管理页面（25分钟）**
   - 左右分栏布局
   - 类型管理
   - 数据管理

---

### 阶段3：测试验证（30分钟）

1. **功能测试（15分钟）**
   - CRUD操作测试
   - 缓存测试
   - **控制台检查（必须）**

2. **MCP自动化测试（15分钟）**
   - 页面加载测试
   - 交互功能测试
   - **控制台错误检查（必须）**

---

## 🎯 验收标准

### 功能完整性
- [ ] 系统参数CRUD功能完整
- [ ] 数据字典CRUD功能完整
- [ ] 缓存机制正常工作
- [ ] 初始化数据正确加载

### 代码质量
- [ ] 代码符合COLA架构规范
- [ ] 代码无编译错误
- [ ] 控制台无JavaScript错误
- [ ] API响应时间 < 200ms

### 用户体验
- [ ] 页面加载流畅
- [ ] 操作响应及时
- [ ] 错误提示友好
- [ ] 表单验证完整

---

**设计文档版本：** v1.0  
**最后更新时间：** 2025-11-06  
**设计者：** AI 助手

