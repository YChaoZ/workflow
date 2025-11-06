# 工作流系统

> 基于 COLA 架构 + Flowable + Vue 3 的企业级工作流系统

## 项目简介

这是一个完整的前后端分离工作流系统，采用业界最佳实践和成熟技术栈构建，支持高复杂度的业务流程场景。

## 技术架构

### 后端技术栈

- **架构规范**：阿里 COLA 架构（Clean Object-Oriented & Layered Architecture）
- **工作流引擎**：Flowable 7.0.1
- **框架版本**：Spring Boot 3.2.0
- **JDK 版本**：JDK 17
- **数据库**：MySQL 8.0+
- **ORM 框架**：MyBatis Plus 3.5.5
- **连接池**：Druid 1.2.20
- **构建工具**：Maven 3.8+

### 前端技术栈

- **框架**：Vue 3.5+
- **UI 组件库**：Element Plus 2.4+
- **构建工具**：Vite 7.x
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP 客户端**：Axios
- **流程设计器**：bpmn-js
- **表单设计器**：form-create
- **图表库**：ECharts 5.4
- **语言**：TypeScript

## 项目结构

```
workFolw/
├── backend/                                    # 后端项目（单一工程 COLA 架构）
│   ├── pom.xml                                 # Maven 配置
│   └── src/main/
│       ├── java/com/bank/workflow/
│       │   ├── domain/                         # 领域层（核心业务逻辑）
│       │   │   ├── process/                    # 流程领域
│       │   │   │   ├── entity/                 # 领域实体
│       │   │   │   └── gateway/                # Gateway 接口
│       │   │   └── user/                       # 用户领域
│       │   ├── app/                            # 应用层（业务编排）
│       │   │   └── process/                    # 流程应用服务
│       │   │       ├── command/                # 命令对象
│       │   │       ├── executor/               # 命令执行器
│       │   │       └── ProcessAppService.java
│       │   ├── adapter/                        # 适配层（外部接口）
│       │   │   └── web/                        # Web 控制器
│       │   │       ├── ProcessController.java
│       │   │       ├── config/                 # Web 配置
│       │   │       └── exception/              # 异常处理
│       │   ├── infrastructure/                 # 基础设施层（技术实现）
│       │   │   ├── flowable/config/            # Flowable 配置
│       │   │   └── gateway/                    # Gateway 实现
│       │   └── WorkFlowApplication.java        # 应用启动类
│       └── resources/
│           ├── application.yml                 # 主配置
│           ├── application-dev.yml             # 开发环境
│           ├── application-prod.yml            # 生产环境
│           ├── db/migration/                   # Flyway 数据库脚本
│           │   ├── V1__init_tables.sql
│           │   └── V2__init_data.sql
│           └── processes/                      # BPMN 流程定义
│               └── simple-process.bpmn20.xml
│
├── frontend/                                   # 前端项目（Vue 3）
│   ├── src/
│   │   ├── api/                                # API 接口
│   │   ├── assets/                             # 静态资源
│   │   ├── components/                         # 公共组件
│   │   ├── router/                             # 路由配置
│   │   ├── store/                              # 状态管理
│   │   ├── utils/                              # 工具函数
│   │   └── views/                              # 页面组件
│   ├── package.json
│   └── vite.config.ts
│
└── doc/                                        # 技术文档
    ├── 01-工作流引擎选型分析.md
    ├── 02-前端技术栈选型分析.md
    ├── 03-整体架构建议.md
    └── 04-项目实施路线图.md
```

> **架构说明**：采用单一工程多包结构，通过包名实现 COLA 分层设计，简化配置的同时保持架构清晰。详见 [backend/README.md](backend/README.md)

## 核心功能

### ✅ 已完成（基础架构）

**后端架构**
- ✅ COLA 四层架构（单一工程多包结构，更简洁高效）
- ✅ Maven 依赖配置（Spring Boot 3.2.0 + Flowable 7.0.1）
- ✅ Flowable 引擎集成和配置
- ✅ 数据库设计（15+ 张业务表 + Flowable 70+ 张表）
- ✅ Domain 层（ProcessInstance、User 实体，Gateway 接口）
- ✅ Infrastructure 层（ProcessEngineGatewayImpl、Flowable 配置）
- ✅ App 层（ProcessAppService、StartProcessCmdExe）
- ✅ Adapter 层（ProcessController、跨域配置、全局异常处理）
- ✅ 示例 BPMN 流程文件（simple-process.bpmn20.xml）

**前端架构**
- ✅ Vue 3 + TypeScript + Vite 项目搭建
- ✅ Element Plus UI 组件库集成
- ✅ 完整的路由配置（登录、首页、流程、任务模块）
- ✅ Pinia 状态管理（用户、应用状态）
- ✅ Axios HTTP 请求封装（拦截器、错误处理）
- ✅ 主布局组件（侧边栏、顶部导航、内容区）
- ✅ 登录页面
- ✅ 首页（统计卡片、快捷入口）
- ✅ 占位页面（流程、任务相关页面）

**项目配置**
- ✅ 完整的配置文件（application.yml、环境配置）
- ✅ 数据库初始化脚本（表结构 + 初始数据）
- ✅ 详细的技术文档（选型分析、架构设计、实施路线图）

### 🚧 待实现（按 MVP 阶段）

**MVP1 - 核心流程功能**
- ⏳ 流程启动和查询 API
- ⏳ 任务查询和办理 API
- ⏳ 用户认证（JWT）
- ⏳ 前后端联调

**MVP2 - 流程设计器**
- ⏳ BPMN 流程设计器集成
- ⏳ 流程定义管理
- ⏳ 流程部署和发布
- ⏳ 流程实例跟踪

**MVP3 - 表单和组织架构**
- ⏳ 表单设计器
- ⏳ 动态表单渲染
- ⏳ 组织架构管理
- ⏳ 权限管理

**MVP4 - 高级功能**
- ⏳ 流程监控
- ⏳ 数据分析
- ⏳ 流程回退/撤回
- ⏳ 消息提醒

## 🚀 快速开始

> **重要提示**：项目基础架构已完整搭建完成，可以直接启动运行！
> 
> 详细的启动步骤请查看：[快速启动指南](./QUICK_START.md)

### 环境要求

- ✅ JDK 17+
- ✅ Node.js 18+
- ✅ MySQL 8.0+
- ✅ Maven 3.8+

### 后端启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE workflow_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 修改数据库配置
# 编辑 backend/workflow-start/src/main/resources/application-dev.yml
# 修改数据库连接信息

# 3. 进入后端目录
cd backend

# 4. 编译打包
mvn clean install

# 5. 启动应用
cd workflow-start
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:5173 启动

### 默认账号

- 用户名：`admin`
- 密码：`admin123`

## 开发指南

### 后端开发

#### COLA 架构说明

```
adapter    -> 适配器层：接收外部请求，调用 app 层
app        -> 应用层：编排领域服务，处理事务
domain     -> 领域层：核心业务逻辑，定义 Gateway 接口
infrastructure -> 基础设施层：实现 Gateway，集成外部系统
```

#### 添加新功能

1. 在 `domain` 层定义实体和 Gateway 接口
2. 在 `infrastructure` 层实现 Gateway
3. 在 `app` 层创建应用服务
4. 在 `adapter` 层创建 Controller

### 前端开发

#### 目录规范

- `views/` - 页面组件
- `components/` - 公共组件
- `api/` - API 接口定义
- `store/` - 状态管理
- `router/` - 路由配置

#### 添加新页面

1. 在 `views/` 下创建页面组件
2. 在 `router/index.ts` 中添加路由
3. 在 `api/` 下创建 API 接口

## 文档

详细的技术选型和架构设计文档请查看 `doc/` 目录：

- [工作流引擎选型分析](./doc/01-工作流引擎选型分析.md)
- [前端技术栈选型分析](./doc/02-前端技术栈选型分析.md)
- [整体架构建议](./doc/03-整体架构建议.md)
- [项目实施路线图](./doc/04-项目实施路线图.md)

## 常见问题

### 1. 后端启动失败？

- 检查 JDK 版本是否为 17+
- 检查数据库是否创建
- 检查数据库连接配置是否正确

### 2. 前端启动失败？

- 检查 Node.js 版本是否为 18+
- 删除 `node_modules` 和 `package-lock.json`，重新安装依赖
- 检查端口 5173 是否被占用

### 3. Flowable 表没有自动创建？

- 检查配置文件中 `flowable.database-schema-update` 是否为 `true`
- 检查数据库连接是否正常
- 查看启动日志是否有错误信息

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

如有问题或建议，请提 Issue 或联系项目维护者。

---

**版本**: v1.0.0  
**最后更新**: 2025-11-05

