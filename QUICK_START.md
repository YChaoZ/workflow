# 🚀 快速启动指南

## ✅ 项目已完成搭建

恭喜！工作流系统的完整架构已经搭建完成，包括：

### 后端（已完成）
- ✅ COLA 四层架构（单一工程多包结构）
- ✅ Flowable 引擎集成
- ✅ 数据库脚本（15+ 张业务表）
- ✅ Domain 层（实体、Gateway 接口）
- ✅ Infrastructure 层（Gateway 实现、Flowable 配置）
- ✅ App 层（应用服务、Command、Executor）
- ✅ Adapter 层（Controller、跨域配置、异常处理）
- ✅ 示例 BPMN 流程文件

### 前端（已完成）
- ✅ Vue 3 + TypeScript + Vite
- ✅ Element Plus UI 组件库
- ✅ 完整的路由和状态管理
- ✅ 登录页面和主布局
- ✅ HTTP 请求封装
- ✅ 首页和占位页面

## 📋 环境要求

- ✅ JDK 17+
- ✅ Maven 3.8+
- ✅ MySQL 8.0+
- ✅ Node.js 18+

## 🎯 启动步骤

### 第一步：创建数据库

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE workflow_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 验证数据库
SHOW DATABASES;
```

### 第二步：配置数据库连接

编辑文件：`backend/src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/workflow_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      username: root        # 改为您的 MySQL 用户名
      password: password    # 改为您的 MySQL 密码
```

### 第三步：启动后端

#### 方式一：使用 IDEA（推荐）

1. 用 IntelliJ IDEA 打开 `backend` 目录
2. 等待 Maven 依赖下载完成（首次可能需要 10-20 分钟）
3. 找到 `WorkFlowApplication.java`
4. 右键 -> Run 'WorkFlowApplication'

#### 方式二：使用命令行

```bash
cd backend
mvn clean install
cd workflow-start
mvn spring-boot:run
```

#### 验证后端启动成功

看到以下输出说明启动成功：

```
============================================
工作流系统启动成功！
Workflow System Started Successfully!
============================================
访问地址: http://localhost:8080
接口文档: http://localhost:8080/doc.html
============================================
```

测试接口：
```bash
curl http://localhost:8080/api/process/health
```

预期返回：
```json
{"code":200,"message":"服务正常","timestamp":1699XXX}
```

### 第四步：启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖（首次需要，可能需要 5-10 分钟）
npm install

# 如果安装速度慢，可以使用国内镜像
npm config set registry https://registry.npmmirror.com
npm install

# 启动开发服务器
npm run dev
```

#### 验证前端启动成功

看到以下输出说明启动成功：

```
VITE v7.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
➜  Network: http://192.168.x.x:5173/
➜  press h + enter to show help
```

浏览器会自动打开 http://localhost:5173

### 第五步：登录系统

在登录页面输入：
- 用户名：`admin`
- 密码：`admin123`（暂时前端模拟，后续会实现真实认证）

登录后会进入首页，可以看到：
- 统计卡片（流程定义、待办任务等）
- 快捷入口
- 系统信息

## 🧪 测试功能

### 测试一：健康检查

```bash
# 测试后端健康状态
curl http://localhost:8080/api/process/health
```

### 测试二：启动流程（需要先有流程定义）

```bash
curl -X POST http://localhost:8080/api/process/start \
  -H "Content-Type: application/json" \
  -d '{
    "processKey": "simpleProcess",
    "businessKey": "TEST001",
    "startUser": "admin",
    "variables": {
      "manager": "lisi",
      "ceo": "admin"
    }
  }'
```

注意：首次启动时，Flowable 会自动部署 `simple-process.bpmn20.xml` 流程定义。

### 测试三：查询流程实例

```bash
# 替换 {instanceId} 为上一步返回的流程实例ID
curl http://localhost:8080/api/process/instance/{instanceId}
```

## 📊 查看数据库

```sql
-- 查看已创建的表
USE workflow_dev;
SHOW TABLES;

-- Flowable 核心表（约 70+ 张）
-- ACT_RE_* - 流程定义相关
-- ACT_RU_* - 运行时相关
-- ACT_HI_* - 历史记录相关

-- 业务扩展表（15+ 张）
-- wf_* - 工作流业务表
-- sys_* - 系统管理表

-- 查看流程定义
SELECT * FROM ACT_RE_PROCDEF;

-- 查看流程实例
SELECT * FROM ACT_RU_EXECUTION;

-- 查看任务
SELECT * FROM ACT_RU_TASK;

-- 查看用户
SELECT * FROM sys_user;
```

## ❓ 常见问题

### 问题1：后端启动失败

**错误：com.mysql.cj.jdbc.exceptions.CommunicationsException**
```
解决：
1. 检查 MySQL 是否启动：mysql -u root -p
2. 检查数据库是否存在：SHOW DATABASES;
3. 检查用户名密码是否正确
```

**错误：Failed to determine suitable jdbc url**
```
解决：检查 application-dev.yml 中的数据库配置是否正确
```

**错误：JDK 版本不对**
```
解决：
java -version  # 查看版本，确保是 JDK 17+
```

### 问题2：Maven 依赖下载失败

```bash
# 使用阿里云 Maven 镜像
# 编辑 ~/.m2/settings.xml，添加：
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Maven</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### 问题3：前端启动失败

```bash
# 删除依赖重新安装
rm -rf node_modules package-lock.json
npm install

# 检查 Node.js 版本
node -v  # 应该是 v18 或更高
```

### 问题4：端口被占用

```bash
# 查看端口占用（Mac/Linux）
lsof -i:8080  # 后端端口
lsof -i:5173  # 前端端口

# 杀掉进程
kill -9 <PID>

# 或修改端口
# 后端：修改 application.yml 中的 server.port
# 前端：修改 vite.config.ts 中的 server.port
```

## 🎯 下一步开发

项目基础架构已搭建完成，按照以下顺序进行功能开发：

### MVP1 - 核心流程功能（第1-2周）

1. **完善流程管理 API**
   - [ ] 流程定义列表查询
   - [ ] 流程实例列表查询
   - [ ] 流程历史查询

2. **任务管理 API**
   - [ ] 待办任务查询
   - [ ] 任务完成
   - [ ] 任务转办
   - [ ] 任务委派

3. **前端页面开发**
   - [ ] 流程定义列表页
   - [ ] 流程实例列表页
   - [ ] 待办任务列表页
   - [ ] 任务办理页

### MVP2 - 流程设计器（第3-4周）

1. **集成 bpmn-js**
   - [ ] 流程设计器组件
   - [ ] 流程保存
   - [ ] 流程部署

2. **流程定义管理**
   - [ ] 流程上传
   - [ ] 流程版本管理
   - [ ] 流程启用/禁用

### MVP3 - 表单和组织架构（第5-6周）

1. **表单设计器**
   - [ ] 集成 form-create
   - [ ] 动态表单渲染
   - [ ] 表单数据保存

2. **组织架构**
   - [ ] 部门管理
   - [ ] 用户管理
   - [ ] 角色权限

### MVP4 - 高级功能（第7-8周）

1. **流程监控**
   - [ ] 流程监控大屏
   - [ ] 统计图表

2. **高级特性**
   - [ ] 流程回退
   - [ ] 流程撤回
   - [ ] 消息提醒

## 📚 相关文档

- [项目说明](./README.md)
- [项目搭建指南](./PROJECT_SETUP_GUIDE.md)
- [技术选型文档](./doc/)

## 🎉 总结

恭喜您完成了工作流系统的基础架构搭建！现在您可以：

1. ✅ 启动后端服务（Spring Boot + Flowable）
2. ✅ 启动前端服务（Vue 3 + Element Plus）
3. ✅ 测试基础接口（健康检查、流程启动）
4. ✅ 查看数据库表（Flowable 表 + 业务表）
5. ✅ 开始功能开发（按 MVP 阶段）

如有问题，请参考：
- 📖 技术文档：`doc/` 目录
- 🐛 常见问题：本文档的常见问题章节
- 💬 提问：提交 Issue

祝开发顺利！🚀

