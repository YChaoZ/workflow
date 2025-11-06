# 🚀 MVP2 开发启动

**启动时间**：2025-11-05  
**当前任务**：环境准备

---

## ✅ 第一步：前端依赖安装

### 1. 更新 package.json

需要添加的依赖：
```json
{
  "dependencies": {
    "bpmn-js": "^17.0.2",
    "bpmn-js-properties-panel": "^5.0.0",
    "diagram-js": "^12.0.0"
  },
  "devDependencies": {
    "@types/bpmn-js": "^0.0.10"
  }
}
```

### 2. 安装命令
```bash
cd /Users/yanchao/IdeaProjects/workFolw/frontend
npm install bpmn-js@^17.0.2 bpmn-js-properties-panel@^5.0.0 diagram-js@^12.0.0 --save
npm install @types/bpmn-js@^0.0.10 --save-dev
```

---

## ✅ 第二步：创建目录结构

### 前端目录
```bash
# 流程设计器组件
mkdir -p frontend/src/components/BpmnDesigner
mkdir -p frontend/src/components/BpmnDesigner/utils

# 流程管理页面
mkdir -p frontend/src/views/process/designer
mkdir -p frontend/src/views/process/definition

# API接口
mkdir -p frontend/src/api/definition
```

### 后端目录
```bash
# 流程定义管理
mkdir -p backend/src/main/java/com/bank/workflow/app/definition
mkdir -p backend/src/main/java/com/bank/workflow/app/definition/command
mkdir -p backend/src/main/java/com/bank/workflow/app/definition/query
mkdir -p backend/src/main/java/com/bank/workflow/domain/definition
mkdir -p backend/src/main/java/com/bank/workflow/adapter/web

# 任务高级操作
mkdir -p backend/src/main/java/com/bank/workflow/app/task/comment
mkdir -p backend/src/main/java/com/bank/workflow/app/task/attachment
```

---

## ✅ 第三步：数据库迁移

创建新的Flyway迁移脚本：

### V3__mvp2_tables.sql

```sql
-- MVP2阶段新增表

-- 1. 流程分类表
CREATE TABLE wf_process_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  code VARCHAR(50) NOT NULL COMMENT '分类编码',
  parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
  sort_order INT DEFAULT 0 COMMENT '排序',
  description VARCHAR(500) COMMENT '描述',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_code (code),
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程分类表';

-- 2. 任务意见表
CREATE TABLE wf_task_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
  process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  user_name VARCHAR(100) COMMENT '用户名',
  comment_type VARCHAR(20) NOT NULL COMMENT '意见类型：APPROVE-同意,REJECT-拒绝,TRANSFER-转办',
  comment_text TEXT COMMENT '意见内容',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_process_instance (process_instance_id),
  INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务意见表';

-- 3. 任务附件表
CREATE TABLE wf_task_attachment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
  process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
  file_name VARCHAR(255) NOT NULL COMMENT '文件名',
  file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
  file_size BIGINT COMMENT '文件大小（字节）',
  file_type VARCHAR(50) COMMENT '文件类型',
  uploaded_by BIGINT NOT NULL COMMENT '上传人ID',
  uploaded_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  INDEX idx_process_instance (process_instance_id),
  INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务附件表';

-- 初始化流程分类数据
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) VALUES
('行政管理', 'administration', 0, 1, '行政管理类流程'),
('人事管理', 'hr', 0, 2, '人事管理类流程'),
('财务管理', 'finance', 0, 3, '财务管理类流程'),
('采购管理', 'procurement', 0, 4, '采购管理类流程'),
('项目管理', 'project', 0, 5, '项目管理类流程');

-- 行政管理子分类
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) VALUES
('请假审批', 'leave', (SELECT id FROM (SELECT id FROM wf_process_category WHERE code='administration') t), 1, '各类请假申请'),
('出差申请', 'travel', (SELECT id FROM (SELECT id FROM wf_process_category WHERE code='administration') t), 2, '出差申请流程'),
('用印申请', 'seal', (SELECT id FROM (SELECT id FROM wf_process_category WHERE code='administration') t), 3, '公章使用申请');
```

---

## 📋 执行清单

### 当前正在执行

- [x] 创建MVP2开发计划文档
- [ ] 安装前端依赖
- [ ] 创建目录结构
- [ ] 创建数据库迁移脚本
- [ ] 运行数据库迁移

### 即将开始

- [ ] 实现流程定义管理后端API
- [ ] 实现流程分类管理
- [ ] 集成bpmn-js流程设计器

---

## 🎯 今天的目标

1. ✅ 完成环境准备
2. ✅ 创建数据库表
3. ✅ 实现流程定义管理的基础API（部署、查询）

---

准备就绪，开始编码！🚀

