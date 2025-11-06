# Java版本兼容性问题解决方案

## 🔴 问题根本原因

**您当前使用的是 Java 23.0.2，但Spring Boot 3.2.0官方支持的Java版本为：**
- ✅ **Java 17 (LTS - 推荐)**
- ✅ **Java 21 (LTS - 推荐)**  
- ⚠️  Java 23 (非LTS，兼容性问题)

错误信息：
```
Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

这是MyBatis-Plus的`MapperFactoryBean`在Java 23上的兼容性问题。

## ✅ 解决方案：切换到Java 17

### 方案A：在IDEA中切换Java版本（推荐）

1. **打开Project Structure**
   - 快捷键：`Cmd + ;` (Mac) 或 `Ctrl + Alt + Shift + S` (Windows)

2. **配置Project SDK**
   - 点击 `Project` → `SDK` 下拉框
   - 选择 **Java 17** (如果没有，点击 `Add SDK` → `Download JDK`)
   - 推荐选择：**Eclipse Temurin 17 (OpenJDK 17)** 或 **Oracle JDK 17**

3. **配置Language Level**
   - 在同一页面，`Language level` 选择：**SDK default (17 - Sealed types, always-strict floating-point semantics)**

4. **配置Modules SDK**
   - 点击 `Modules` → 选择 `workflow-system` 模块
   - `Module SDK` 选择：**Project SDK (java version "17")**

5. **重新编译并运行**

### 方案B：使用命令行指定Java版本

如果您的系统安装了多个Java版本，可以通过环境变量切换：

```bash
# 查找Java 17安装路径 (macOS)
/usr/libexec/java_home -V

# 查找Java 17安装路径 (Linux)
update-java-alternatives -l

# 临时切换到Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 验证Java版本
java -version

# 重新编译运行
cd /Users/yanchao/IdeaProjects/workFolw/backend
mvn clean package -DskipTests
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 方案C：安装Java 17 (如果没有)

**macOS (使用Homebrew):**
```bash
# 安装Java 17
brew install openjdk@17

# 设置环境变量
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证
java -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
sudo update-alternatives --config java
```

**Windows:**
1. 下载：https://adoptium.net/temurin/releases/?version=17
2. 安装后配置环境变量 `JAVA_HOME`
3. 重启IDEA

## 📋 版本兼容性说明

| 组件 | 当前版本 | 支持的Java版本 |
|------|---------|---------------|
| Spring Boot | 3.2.0 | Java 17, 21 |
| Flowable | 7.0.1 | Java 17+ |
| MyBatis-Plus | 3.5.9 | Java 17+ |
| Lombok | 1.18.30 | Java 17+ |

### 为什么选择Java 17？

1. **LTS (Long Term Support)** - 长期支持版本，稳定可靠
2. **最佳兼容性** - Spring Boot 3.x系列的主要开发和测试版本
3. **广泛使用** - 企业级应用的标准选择
4. **性能优秀** - 相比Java 11提升显著，相比Java 21更成熟

### Java 21 vs Java 17？

- **Java 17** ✅ 更稳定，生态最完善（**推荐**）
- **Java 21** ✅ 也是LTS，功能更新，但部分库可能还在适配

## 🚀 修复后验证

切换Java版本后，执行以下步骤验证：

### 1. 验证Java版本
```bash
java -version
# 应该显示: openjdk version "17.x.x" 或 java version "17.x.x"
```

### 2. 清理并重新编译
```bash
cd /Users/yanchao/IdeaProjects/workFolw/backend
mvn clean install -DskipTests -U
```

### 3. 启动应用
```bash
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 4. 验证启动成功
您应该看到：
```
============================================
工作流系统启动成功！
Workflow System Started Successfully!
============================================
访问地址: http://localhost:9099
接口文档: http://localhost:9099/doc.html
Druid监控: http://localhost:9099/druid/
============================================
```

## 📝 后续步骤

切换Java版本并成功启动后，我们将继续：
1. ✅ 验证后端API正常工作
2. 🎨 开始MVP2前端开发（集成bpmn-js流程设计器）
3. 🔗 前后端联调测试

---

**重要提示：** 
- Java版本切换后，需要在IDEA中 `File` → `Invalidate Caches` → `Invalidate and Restart` 清理缓存
- Maven本地仓库不需要重新下载，所有依赖都兼容Java 17

**创建时间**: 2025-11-05 19:19  
**状态**: ⏳ 等待用户切换Java版本

