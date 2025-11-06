# 工作流系统后端项目

## 📋 项目简介

基于 **阿里 COLA 架构** 和 **Flowable 引擎** 的企业级工作流系统后端。

## 🏗️ 架构设计

### COLA 分层架构

本项目采用单一工程、多包结构，遵循 COLA（Clean Object-oriented and Layered Architecture）架构设计：

```
backend/
├── pom.xml                          # 统一依赖管理
└── src/main/java/com/bank/workflow/
    ├── domain/                      # 领域层 - 核心业务逻辑
    │   ├── process/                 # 流程领域
    │   │   ├── entity/              # 领域实体
    │   │   ├── gateway/             # 防腐层接口
    │   │   └── service/             # 领域服务
    │   ├── task/                    # 任务领域
    │   └── user/                    # 用户领域
    │
    ├── app/                         # 应用层 - 业务编排
    │   ├── dto/                     # 数据传输对象
    │   ├── process/                 # 流程应用服务
    │   │   ├── command/             # 命令对象
    │   │   ├── executor/            # 命令执行器
    │   │   ├── query/               # 查询对象
    │   │   └── ProcessAppService.java
    │   └── task/                    # 任务应用服务
    │
    ├── adapter/                     # 适配层 - 外部交互
    │   ├── web/                     # Web适配器
    │   │   ├── config/              # Web配置
    │   │   ├── exception/           # 异常处理
    │   │   └── *Controller.java     # REST控制器
    │   ├── converter/               # 数据转换器
    │   └── event/                   # 事件适配器
    │
    ├── infrastructure/              # 基础设施层 - 技术实现
    │   ├── flowable/                # Flowable集成
    │   │   └── config/              # Flowable配置
    │   ├── gateway/                 # Gateway实现
    │   ├── mapper/                  # MyBatis Mapper
    │   └── repository/              # 仓储实现
    │
    └── WorkFlowApplication.java     # 应用启动类
```

## 🎯 COLA 分层职责

### 1. Domain 层（领域层）
**职责**：核心业务逻辑，不依赖任何外部框架

- **Entity**：领域实体，包含业务规则
- **Gateway**：防腐层接口，定义与外部交互的抽象
- **Service**：领域服务，处理复杂业务逻辑

**特点**：
- ✅ 纯 Java 对象，无框架注解
- ✅ 包含核心业务规则
- ✅ 不依赖其他层

### 2. App 层（应用层）
**职责**：业务编排，协调领域对象完成业务用例

- **Command**：命令对象，封装用户意图
- **Executor**：命令执行器，编排业务流程
- **Query**：查询对象，封装查询请求
- **AppService**：应用服务，对外提供业务能力

**特点**：
- ✅ 依赖 Domain 层
- ✅ 使用 `@Service`、`@Transactional`
- ✅ 实现业务用例编排

### 3. Adapter 层（适配层）
**职责**：处理外部请求，转换为领域语言

- **Web**：REST API 控制器
- **Converter**：DTO 与领域对象转换
- **Event**：事件发布与订阅

**特点**：
- ✅ 依赖 App 层
- ✅ 使用 `@RestController`、`@RequestMapping`
- ✅ 处理 HTTP 请求响应

### 4. Infrastructure 层（基础设施层）
**职责**：提供技术能力支持

- **Gateway实现**：实现 Domain 层定义的 Gateway 接口
- **Mapper**：数据库访问（MyBatis）
- **Config**：技术组件配置（Flowable、Redis 等）

**特点**：
- ✅ 实现 Domain 层的 Gateway 接口
- ✅ 与具体技术框架集成
- ✅ 提供技术基础能力

## 📦 技术栈

### 核心框架
- **Spring Boot 3.2.0** - 应用框架
- **COLA 4.3.2** - 架构规范
- **Flowable 7.0.1** - 工作流引擎

### 数据库
- **MySQL 8.0+** - 主数据库
- **MyBatis Plus 3.5.5** - ORM 框架
- **Druid 1.2.20** - 数据库连接池
- **Flyway** - 数据库版本管理

### 工具库
- **Lombok 1.18.30** - 代码简化
- **Hutool 5.8.23** - 工具集合
- **FastJSON2 2.0.43** - JSON 处理
- **JWT 0.12.3** - 认证授权

## 🚀 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 2. 克隆项目
```bash
cd backend
```

### 3. 配置数据库
创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS workflow_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;
```

修改配置文件 `src/main/resources/application-dev.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflow_db?serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    username: root
    password: your_password
```

### 4. 编译项目
```bash
mvn clean compile
```

### 5. 启动应用
```bash
# 方式1：使用 Maven
mvn spring-boot:run

# 方式2：打包运行
mvn clean package -DskipTests
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar
```

应用启动后：
- 后端地址：http://localhost:8080
- Flowable 会自动创建约 70 张表
- Flyway 会自动执行数据库迁移

### 6. 验证
```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 测试接口
curl http://localhost:8080/api/process/definitions
```

## 📁 重要目录说明

### 配置文件
```
src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境配置
├── application-prod.yml         # 生产环境配置
├── db/migration/                # Flyway 数据库迁移脚本
│   ├── V1__init_tables.sql      # 初始化表结构
│   └── V2__init_data.sql        # 初始化数据
└── processes/                   # BPMN 流程定义文件
    └── simple-process.bpmn20.xml
```

### 数据库表
项目会自动创建约 85 张表：
- **业务表（15张）**：`wf_*`、`sys_*` 开头
- **Flowable表（70张）**：`ACT_*`、`FLW_*` 开头

详细说明请查看：`src/main/resources/db/migration/README.md`

## 🔧 开发指南

### 添加新功能的步骤

#### 1. 创建领域实体（Domain Layer）
```java
// domain/task/entity/Task.java
@Data
public class Task {
    private String taskId;
    private String taskName;
    // 业务规则方法
    public void complete() {
        // 领域逻辑
    }
}
```

#### 2. 定义 Gateway 接口（Domain Layer）
```java
// domain/task/gateway/TaskGateway.java
public interface TaskGateway {
    Task getById(String taskId);
    void save(Task task);
}
```

#### 3. 实现 Gateway（Infrastructure Layer）
```java
// infrastructure/gateway/TaskGatewayImpl.java
@Component
public class TaskGatewayImpl implements TaskGateway {
    @Autowired
    private TaskService taskService; // Flowable
    
    @Override
    public Task getById(String taskId) {
        // 实现逻辑
    }
}
```

#### 4. 创建命令和执行器（App Layer）
```java
// app/task/command/CompleteTaskCmd.java
@Data
public class CompleteTaskCmd {
    private String taskId;
    private Map<String, Object> variables;
}

// app/task/executor/CompleteTaskCmdExe.java
@Component
public class CompleteTaskCmdExe {
    @Autowired
    private TaskGateway taskGateway;
    
    public void execute(CompleteTaskCmd cmd) {
        // 业务编排
    }
}
```

#### 5. 创建应用服务（App Layer）
```java
// app/task/TaskAppService.java
@Service
public class TaskAppService {
    @Autowired
    private CompleteTaskCmdExe completeTaskCmdExe;
    
    @Transactional
    public void completeTask(CompleteTaskCmd cmd) {
        completeTaskCmdExe.execute(cmd);
    }
}
```

#### 6. 创建控制器（Adapter Layer）
```java
// adapter/web/TaskController.java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskAppService taskAppService;
    
    @PostMapping("/{taskId}/complete")
    public Response complete(@PathVariable String taskId) {
        // 调用应用服务
    }
}
```

### 分层依赖规则

```
Adapter Layer
    ↓ (依赖)
  App Layer
    ↓ (依赖)
 Domain Layer
    ↑ (实现)
Infrastructure Layer
```

**重要原则：**
- ✅ Adapter → App → Domain → 无依赖
- ✅ Infrastructure → Domain（实现 Gateway）
- ❌ Domain 不能依赖任何其他层
- ❌ 下层不能依赖上层

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 跳过测试编译
mvn clean package -DskipTests

# 运行单个测试类
mvn test -Dtest=ProcessAppServiceTest
```

## 📦 打包部署

### 开发环境
```bash
mvn clean package -Pdev -DskipTests
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 生产环境
```bash
mvn clean package -Pprod -DskipTests
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📚 相关文档

- [数据库初始化指南](../数据库初始化指南.md)
- [项目快速启动](../QUICK_START.md)
- [COLA 架构文档](https://github.com/alibaba/COLA)
- [Flowable 官方文档](https://www.flowable.com/open-source/docs)

## ❓ 常见问题

### Q1: 为什么不用 Maven 多模块？
**A**: 对于中小型项目，单一工程更简单高效。通过包结构同样可以实现分层隔离，并且编译更快、配置更简单。

### Q2: 如何保证分层不被破坏？
**A**: 可以使用 ArchUnit 编写架构测试，自动检测违反分层规则的代码。

### Q3: Domain 层为什么不能有注解？
**A**: Domain 层是纯业务逻辑，不应该依赖任何框架。这样可以保证业务逻辑的可移植性和可测试性。

### Q4: Gateway 和 Repository 有什么区别？
**A**: Gateway 是 Domain 层定义的接口，更业务化；Repository 是 Infrastructure 层的实现，更技术化。Gateway 可以有多种实现（数据库、HTTP、缓存等）。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

