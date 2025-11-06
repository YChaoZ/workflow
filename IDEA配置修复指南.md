# 🔧 IDEA 配置修复指南

## 问题描述

报错：`JDK isn't specified for module 'workflow-parent'`

## 原因分析

项目已从 **Maven 多模块** 重构为 **单一工程**，但 IntelliJ IDEA 还保留着旧的模块配置，导致引用了不存在的 `workflow-parent` 模块。

## 解决方案

### 方案一：重新导入项目（推荐）⭐

#### 步骤 1：关闭项目
```
File → Close Project
```

#### 步骤 2：删除 IDEA 配置文件
```bash
# 在项目根目录执行
cd /Users/yanchao/IdeaProjects/workFolw
rm -rf .idea
rm -rf *.iml
rm -rf backend/.idea
rm -rf backend/*.iml
```

#### 步骤 3：重新导入
```
1. 打开 IntelliJ IDEA
2. File → Open
3. 选择 workFolw 目录
4. 选择 "Open as Project"
5. 等待 IDEA 自动导入 Maven 项目
```

#### 步骤 4：配置 JDK
```
1. File → Project Structure (Ctrl+Alt+Shift+S)
2. Project Settings → Project
3. SDK: 选择 JDK 17
4. Language Level: 17 - Sealed types, always-strict floating-point semantics
5. 点击 OK
```

#### 步骤 5：配置 Maven
```
1. File → Settings (Ctrl+Alt+S)
2. Build, Execution, Deployment → Build Tools → Maven
3. Maven home path: 确认 Maven 路径正确
4. User settings file: 确认 settings.xml 路径
5. Local repository: 确认本地仓库路径
6. 点击 OK
```

#### 步骤 6：刷新 Maven
```
1. 打开右侧 Maven 面板
2. 点击刷新图标（Reload All Maven Projects）
3. 等待依赖下载完成
```

---

### 方案二：手动修复（快速）

#### 步骤 1：打开模块设置
```
File → Project Structure → Modules
```

#### 步骤 2：删除旧模块
找到并删除以下模块：
- workflow-parent
- workflow-domain
- workflow-app
- workflow-adapter
- workflow-infrastructure
- workflow-start

**操作**：选中模块 → 点击减号 `-` → Remove

#### 步骤 3：添加新模块
```
1. 点击加号 `+`
2. Import Module
3. 选择 backend/pom.xml
4. 选择 "Import module from external model" → Maven
5. 点击 Finish
```

#### 步骤 4：配置 JDK
```
1. Project Settings → Project
2. SDK: 选择 JDK 17
3. 点击 OK
```

#### 步骤 5：刷新项目
```
File → Invalidate Caches / Restart → Invalidate and Restart
```

---

### 方案三：命令行验证（确认项目本身没问题）

```bash
# 进入后端目录
cd /Users/yanchao/IdeaProjects/workFolw/backend

# 清理并编译
mvn clean compile

# 如果编译成功，说明项目本身没问题，只是 IDEA 配置问题
```

**预期输出**：
```
[INFO] BUILD SUCCESS
[INFO] Total time: 1.2s
[INFO] Compiling 12 source files
```

---

## 验证修复成功

### 1. 检查项目结构
IDEA 左侧项目树应该显示：
```
workFolw
├── backend
│   ├── src
│   │   └── main
│   │       ├── java
│   │       └── resources
│   └── pom.xml
└── frontend
```

### 2. 检查 Maven 面板
右侧 Maven 面板应该显示：
```
workflow-system (root)
├── Lifecycle
├── Plugins
└── Dependencies
```

### 3. 编译测试
```
1. 右键 backend 目录
2. Maven → Reload Project
3. 右键 WorkFlowApplication.java
4. Run 'WorkFlowApplication'
```

应该能正常启动，看到：
```
Started WorkFlowApplication in 3.5 seconds
```

---

## 常见问题

### Q1: 删除 .idea 后会丢失配置吗？
**A**: 不会丢失重要配置。项目的核心配置（Maven、代码）都在 pom.xml 和源代码中，IDEA 会自动重新生成 .idea 配置。

### Q2: 如果还是报错怎么办？
**A**: 尝试以下步骤：
1. 确认 JDK 17 已正确安装：`java -version`
2. 确认 Maven 配置正确：`mvn -v`
3. 清理 Maven 本地仓库的项目缓存：`rm -rf ~/.m2/repository/com/bank/workflow`
4. 重启 IDEA

### Q3: 前端项目也需要重新导入吗？
**A**: 前端项目未变化，不需要。如果前端有问题，可以：
```bash
cd frontend
rm -rf node_modules
npm install
```

---

## 推荐的 IDEA 配置

### Maven 设置
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven

✅ Maven home path: /usr/local/Cellar/maven/3.x.x (根据实际路径)
✅ User settings file: ~/.m2/settings.xml
✅ Local repository: ~/.m2/repository
✅ Threads: 4 (根据 CPU 核心数)
✅ VM options for importer: -Xmx1024m
```

### JDK 设置
```
File → Project Structure → SDKs

✅ 添加 JDK 17
✅ 路径示例：/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

### 编译器设置
```
File → Settings → Build, Execution, Deployment → Compiler

✅ Build project automatically (开启自动编译)
✅ Compile independent modules in parallel (并行编译)
✅ Heap size: 2048 MB
```

---

## 一键修复脚本

如果不想手动操作，可以执行这个脚本：

```bash
#!/bin/bash
# 保存为 fix-idea-config.sh

cd /Users/yanchao/IdeaProjects/workFolw

echo "🔧 开始修复 IDEA 配置..."

# 1. 删除 IDEA 配置文件
echo "📁 删除旧的 IDEA 配置..."
rm -rf .idea
rm -rf *.iml
rm -rf backend/.idea
rm -rf backend/*.iml

# 2. 清理 Maven 缓存
echo "🧹 清理 Maven 缓存..."
rm -rf ~/.m2/repository/com/bank/workflow

# 3. 重新编译
echo "🔨 重新编译项目..."
cd backend
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ 编译成功！"
    echo "📝 请重新用 IDEA 打开项目："
    echo "   File → Open → 选择 workFolw 目录"
else
    echo "❌ 编译失败，请检查 Maven 配置"
    exit 1
fi

echo "🎉 修复完成！"
```

**使用方法**：
```bash
chmod +x fix-idea-config.sh
./fix-idea-config.sh
```

---

## 完成后的检查清单

- [ ] IDEA 左侧显示 backend 项目结构正常
- [ ] Maven 面板显示 workflow-system (root)
- [ ] 没有报 JDK 错误
- [ ] 可以正常编译（Ctrl+F9）
- [ ] 可以运行 WorkFlowApplication
- [ ] 代码没有红色波浪线

全部打勾后，配置修复完成！✅

---

**如有问题，请查看日志或联系技术支持。**

