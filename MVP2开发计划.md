# 🚀 MVP2 开发计划：流程设计器 + 任务管理

**阶段**：MVP2（第5-8周）  
**开始时间**：2025-11-05  
**预计完成**：4周后  
**核心目标**：实现可视化流程设计器，完善任务管理功能

---

## 📋 总体目标

### 核心交付物
1. ✅ 可视化BPMN流程设计器
2. ✅ 完整的流程定义管理
3. ✅ 流程全生命周期支持
4. ✅ 高级任务管理功能
5. ✅ 流程实例跟踪可视化

### 技术栈新增
- **前端**：bpmn-js（流程设计器）
- **前端**：bpmn-js-properties-panel（属性面板）
- **后端**：Flowable流程图生成API

---

## 🎯 功能清单

### 一、后端功能（7项）

#### 1.1 流程定义管理 ⭐ 核心
**功能点**：
- [ ] 流程定义部署（上传BPMN XML）
- [ ] 流程定义删除（级联删除实例）
- [ ] 流程定义启用/禁用（状态管理）
- [ ] 流程定义查询（分页、筛选）
- [ ] 流程定义详情（包含XML内容）

**API设计**：
```
POST   /api/process/definition/deploy        部署流程
DELETE /api/process/definition/{id}          删除流程
PUT    /api/process/definition/{id}/state    启用/禁用
GET    /api/process/definition/list          查询列表
GET    /api/process/definition/{id}          查询详情
GET    /api/process/definition/{id}/xml      获取XML
```

**工作量**：3天

---

#### 1.2 流程分类管理
**功能点**：
- [ ] 流程分类CRUD
- [ ] 分类树形结构
- [ ] 流程与分类关联

**数据库设计**：
```sql
CREATE TABLE wf_process_category (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100),
  code VARCHAR(50),
  parent_id BIGINT,
  sort_order INT,
  description VARCHAR(500),
  created_time DATETIME,
  updated_time DATETIME
);
```

**工作量**：1天

---

#### 1.3 流程版本管理 ⭐ 核心
**功能点**：
- [ ] 同一流程多版本管理
- [ ] 版本号自动递增
- [ ] 版本对比
- [ ] 版本回滚
- [ ] 默认版本设置

**API设计**：
```
GET  /api/process/definition/{key}/versions     版本列表
POST /api/process/definition/{key}/version      部署新版本
GET  /api/process/definition/version/compare    版本对比
POST /api/process/definition/version/{id}/active 设为默认
```

**工作量**：2天

---

#### 1.4 任务高级操作
**功能点**：
- [ ] 任务转办（transfer）
- [ ] 任务委派（delegate）
- [ ] 任务加签（前加签、后加签、并行加签）
- [ ] 任务退回（退回到指定节点）

**API设计**：
```
POST /api/task/{id}/transfer        转办
POST /api/task/{id}/delegate        委派
POST /api/task/{id}/add-sign        加签
POST /api/task/{id}/return          退回
```

**技术要点**：
- 转办：修改任务assignee
- 委派：创建子任务
- 加签：动态添加审批节点
- 退回：使用Flowable的runtimeService跳转

**工作量**：3天

---

#### 1.5 任务意见和附件
**功能点**：
- [ ] 任务意见（审批意见）录入
- [ ] 意见查询（按流程实例）
- [ ] 附件上传（支持多文件）
- [ ] 附件下载
- [ ] 附件删除

**数据库设计**：
```sql
CREATE TABLE wf_task_comment (
  id BIGINT PRIMARY KEY,
  task_id VARCHAR(64),
  process_instance_id VARCHAR(64),
  user_id BIGINT,
  comment_type VARCHAR(20),
  comment_text TEXT,
  created_time DATETIME
);

CREATE TABLE wf_task_attachment (
  id BIGINT PRIMARY KEY,
  task_id VARCHAR(64),
  process_instance_id VARCHAR(64),
  file_name VARCHAR(255),
  file_path VARCHAR(500),
  file_size BIGINT,
  file_type VARCHAR(50),
  uploaded_by BIGINT,
  uploaded_time DATETIME
);
```

**工作量**：2天

---

#### 1.6 历史流程查询 ✅ 已完成
**功能点**：
- [x] 历史流程实例列表
- [x] 历史流程实例详情
- [x] 历史活动实例（执行轨迹）
- [x] 多条件筛选和分页

**状态**：MVP1已实现 ✅

---

#### 1.7 流程图生成 ⭐ 核心
**功能点**：
- [ ] 生成流程图（SVG/PNG）
- [ ] 高亮当前节点
- [ ] 高亮已完成节点
- [ ] 显示执行轨迹

**API设计**：
```
GET /api/process/instance/{id}/diagram          获取流程图（高亮）
GET /api/process/definition/{id}/diagram        获取定义流程图
```

**技术实现**：
```java
// 使用Flowable ProcessDiagramGenerator
ProcessDiagramGenerator diagramGenerator = 
    processEngineConfiguration.getProcessDiagramGenerator();

InputStream diagram = diagramGenerator.generateDiagram(
    bpmnModel,
    "png",
    activeActivityIds,      // 当前活动节点
    highLightedFlows,       // 已执行的连线
    "宋体", "宋体", "宋体",
    null, 1.0, true
);
```

**工作量**：2天

---

### 二、前端功能（8项）

#### 2.1 BPMN流程设计器集成 ⭐ 核心
**功能点**：
- [ ] bpmn-js库集成
- [ ] 基础绘图功能（拖拽、连线）
- [ ] BPMN元素（任务、网关、事件）
- [ ] 画布操作（缩放、平移、撤销/重做）
- [ ] 键盘快捷键

**依赖安装**：
```bash
npm install bpmn-js@^17.0.2
npm install bpmn-js-properties-panel@^5.0.0
```

**组件结构**：
```
src/components/BpmnDesigner/
├── index.vue               # 设计器主组件
├── Toolbar.vue            # 工具栏
├── PropertiesPanel.vue    # 属性面板
├── Palette.vue            # 元素面板
└── utils/
    ├── BpmnModeler.ts     # Modeler封装
    └── customTranslate.ts # 中文翻译
```

**工作量**：3天

---

#### 2.2 流程设计器工具栏
**功能点**：
- [ ] 新建流程
- [ ] 打开流程（导入XML）
- [ ] 保存流程
- [ ] 另存为
- [ ] 导出XML
- [ ] 导出SVG/PNG
- [ ] 撤销/重做
- [ ] 放大/缩小/适应画布
- [ ] 对齐工具
- [ ] 预览

**工作量**：2天

---

#### 2.3 流程设计器属性面板
**功能点**：
- [ ] 基础属性（ID、名称、描述）
- [ ] 任务节点属性：
  - 任务类型（用户任务、服务任务）
  - 办理人（候选人、候选组）
  - 表单配置
  - 监听器
- [ ] 网关属性：
  - 条件表达式
  - 默认路径
- [ ] 事件属性：
  - 定时器
  - 消息
  - 信号

**工作量**：4天

---

#### 2.4 流程定义管理页面
**功能点**：
- [ ] 流程定义列表（表格）
- [ ] 搜索和筛选（名称、分类、状态）
- [ ] 分页
- [ ] 操作按钮：
  - 设计/编辑
  - 部署
  - 启用/禁用
  - 删除
  - 版本管理
  - 预览
  - 导出

**页面路由**：
```
/process/definition/list      # 列表页
/process/definition/design    # 设计页
/process/definition/:id       # 详情页
```

**工作量**：3天

---

#### 2.5 流程部署和发布
**功能点**：
- [ ] 上传BPMN文件
- [ ] XML验证
- [ ] 部署确认（选择分类、设置名称）
- [ ] 部署进度显示
- [ ] 部署结果反馈

**工作量**：1天

---

#### 2.6 流程实例列表（完善）
**功能点**：
- [ ] 实例状态标签（运行中、已完成、已挂起）
- [ ] 流程图预览（缩略图）
- [ ] 快速操作（挂起、激活、删除、查看详情）
- [ ] 批量操作
- [ ] 导出Excel

**工作量**：2天

---

#### 2.7 流程实例详情（流程图高亮）⭐ 核心
**功能点**：
- [ ] 流程图展示（SVG）
- [ ] 高亮当前节点（绿色）
- [ ] 高亮已完成节点（蓝色）
- [ ] 高亮已执行连线
- [ ] 显示节点执行时间
- [ ] 点击节点查看详情
- [ ] 执行轨迹时间线
- [ ] 流程变量查看

**技术实现**：
```vue
<template>
  <div class="process-diagram">
    <div v-html="diagramSvg" @click="handleNodeClick"></div>
    <div class="timeline">
      <el-timeline>
        <el-timeline-item v-for="activity in activities" :key="activity.id">
          {{ activity.name }} - {{ activity.startTime }}
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>
```

**工作量**：4天

---

#### 2.8 任务管理页面完善
**功能点**：
- [ ] 任务详情弹窗（审批表单）
- [ ] 任务转办对话框（选择用户）
- [ ] 任务委派对话框
- [ ] 任务加签对话框（前加签/后加签）
- [ ] 审批意见输入框
- [ ] 附件上传组件
- [ ] 历史意见查看
- [ ] 流程图查看（当前节点）

**工作量**：3天

---

## 📊 开发排期

### 第5周：后端基础API + 前端设计器集成

| 任务 | 工作量 | 开发人员 |
|------|--------|---------|
| 流程定义管理API | 3天 | 后端 |
| 流程分类管理 | 1天 | 后端 |
| bpmn-js集成 | 3天 | 前端 |

**里程碑**：流程设计器可用，可以设计简单流程

---

### 第6周：流程部署 + 版本管理

| 任务 | 工作量 | 开发人员 |
|------|--------|---------|
| 流程版本管理API | 2天 | 后端 |
| 流程图生成API | 2天 | 后端 |
| 设计器工具栏 | 2天 | 前端 |
| 流程定义管理页面 | 3天 | 前端 |

**里程碑**：可以部署和管理流程定义

---

### 第7周：任务高级功能

| 任务 | 工作量 | 开发人员 |
|------|--------|---------|
| 任务高级操作API | 3天 | 后端 |
| 任务意见和附件API | 2天 | 后端 |
| 设计器属性面板 | 4天 | 前端 |

**里程碑**：任务管理功能完善

---

### 第8周：流程实例可视化 + 联调测试

| 任务 | 工作量 | 开发人员 |
|------|--------|---------|
| 流程实例列表完善 | 2天 | 前端 |
| 流程实例详情（高亮） | 4天 | 前端 |
| 任务管理页面完善 | 3天 | 前端 |
| 前后端联调 | 3天 | 全员 |
| 测试和修复Bug | 3天 | 测试 |

**里程碑**：MVP2完成，系统可完整演示

---

## 🗂️ 数据库设计

### 新增表

#### 1. 流程分类表
```sql
CREATE TABLE wf_process_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  code VARCHAR(50) NOT NULL COMMENT '分类编码',
  parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
  sort_order INT DEFAULT 0 COMMENT '排序',
  description VARCHAR(500) COMMENT '描述',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程分类表';
```

#### 2. 任务意见表
```sql
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
```

#### 3. 任务附件表
```sql
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
```

---

## 🎯 关键技术点

### 1. BPMN设计器中文化
```javascript
// 自定义翻译
import translations from './translations'

export default function customTranslate(template, replacements) {
  replacements = replacements || {}
  template = translations[template] || template
  return template.replace(/{([^}]+)}/g, function(_, key) {
    return replacements[key] || '{' + key + '}'
  })
}

// 使用
const modeler = new BpmnModeler({
  additionalModules: [
    {
      translate: ['value', customTranslate]
    }
  ]
})
```

---

### 2. 流程图高亮算法
```javascript
// 获取活动节点ID
const activeActivityIds = await processApi.getActiveActivityIds(instanceId)

// 获取已执行的连线
const highLightedFlows = await processApi.getHighLightedFlows(instanceId)

// 在SVG中添加高亮样式
const addHighlight = (svg, elementIds, color) => {
  elementIds.forEach(id => {
    const element = svg.querySelector(`[data-element-id="${id}"]`)
    if (element) {
      element.style.stroke = color
      element.style.strokeWidth = '3px'
    }
  })
}
```

---

### 3. 流程版本管理策略
```java
/**
 * 部署新版本流程
 * 规则：
 * 1. 同一流程key，自动递增版本号
 * 2. 新版本默认为非激活状态
 * 3. 可手动设置为默认版本
 */
public String deployNewVersion(String processKey, InputStream bpmnStream) {
    // 获取当前最新版本
    ProcessDefinition latestVersion = repositoryService
        .createProcessDefinitionQuery()
        .processDefinitionKey(processKey)
        .latestVersion()
        .singleResult();
    
    // 部署新版本
    Deployment deployment = repositoryService.createDeployment()
        .addInputStream(processKey + ".bpmn20.xml", bpmnStream)
        .deploy();
    
    return deployment.getId();
}
```

---

## ✅ 验收标准

### 功能验收

#### 流程设计器
- [ ] 可以拖拽创建流程元素
- [ ] 可以配置节点属性
- [ ] 可以保存和导出流程
- [ ] 可以导入已有流程
- [ ] 支持中文界面

#### 流程管理
- [ ] 可以部署流程定义
- [ ] 可以查看流程版本
- [ ] 可以启用/禁用流程
- [ ] 可以删除流程
- [ ] 可以导出流程

#### 任务管理
- [ ] 可以转办任务
- [ ] 可以委派任务
- [ ] 可以录入审批意见
- [ ] 可以上传附件
- [ ] 可以查看历史意见

#### 流程跟踪
- [ ] 可以查看流程实例列表
- [ ] 可以查看流程图（高亮当前节点）
- [ ] 可以查看执行轨迹
- [ ] 可以查看流程变量

---

### 性能验收
- [ ] 流程图加载时间 < 2秒
- [ ] 设计器操作响应 < 500ms
- [ ] 流程部署时间 < 3秒
- [ ] 任务操作响应 < 1秒

---

### 质量验收
- [ ] 代码覆盖率 > 70%
- [ ] 无严重Bug
- [ ] 无安全漏洞
- [ ] 通过UAT测试

---

## 🚀 快速开始

### 1. 前端依赖安装

```bash
cd frontend

# 安装bpmn.js相关依赖
npm install bpmn-js@^17.0.2 --save
npm install bpmn-js-properties-panel@^5.0.0 --save

# 安装其他可能需要的依赖
npm install diagram-js@^12.0.0 --save
npm install @types/bpmn-js --save-dev
```

---

### 2. 创建流程设计器组件

```bash
# 创建组件目录
mkdir -p frontend/src/components/BpmnDesigner
mkdir -p frontend/src/views/process/designer

# 创建基础文件
touch frontend/src/components/BpmnDesigner/index.vue
touch frontend/src/components/BpmnDesigner/Toolbar.vue
touch frontend/src/components/BpmnDesigner/PropertiesPanel.vue
```

---

### 3. 后端创建新包结构

```bash
# 流程定义管理
mkdir -p backend/src/main/java/com/bank/workflow/app/definition
mkdir -p backend/src/main/java/com/bank/workflow/domain/definition

# 任务高级操作
mkdir -p backend/src/main/java/com/bank/workflow/app/task/advanced
```

---

## 📚 参考文档

### 官方文档
- [bpmn-js官方文档](https://bpmn.io/toolkit/bpmn-js/)
- [Flowable用户手册](https://www.flowable.com/open-source/docs/bpmn/ch07a-BPMN-Introduction)
- [Element Plus组件库](https://element-plus.org/)

### 示例项目
- [bpmn-js-examples](https://github.com/bpmn-io/bpmn-js-examples)
- [vue-bpmn](https://github.com/xlsdg/vue-bpmn)

---

## 🎊 MVP2完成标志

当以下所有功能都可以演示时，即为MVP2完成：

1. ✅ 在设计器中创建一个请假流程
2. ✅ 部署该流程
3. ✅ 启动该流程实例
4. ✅ 查看流程实例（流程图高亮当前节点）
5. ✅ 办理任务（录入意见、上传附件）
6. ✅ 转办任务
7. ✅ 查看流程执行轨迹
8. ✅ 流程完成后查看历史

---

**准备好了吗？让我们开始MVP2的开发之旅！** 🚀

*创建时间：2025-11-05*  
*预计完成：4周后*

