#!/bin/bash

# 工作流系统后端启动脚本
# 使用Java 17运行

echo "=========================================="
echo "      工作流系统后端启动脚本"
echo "=========================================="

# Java 17路径
JAVA_17_HOME="/opt/homebrew/Cellar/openjdk@17/17.0.15/libexec/openjdk.jdk/Contents/Home"

# 检查Java 17是否存在
if [ ! -d "$JAVA_17_HOME" ]; then
  echo "❌ 错误: Java 17未找到！"
  echo "请安装Java 17或更新JAVA_17_HOME变量"
  exit 1
fi

# 进入backend目录
cd "$(dirname "$0")"

# 检查jar文件是否存在
if [ ! -f "target/workflow-system-1.0.0-SNAPSHOT.jar" ]; then
  echo "📦 JAR文件不存在，开始编译..."
  export JAVA_HOME="$JAVA_17_HOME"
  mvn clean package -DskipTests
  if [ $? -ne 0 ]; then
    echo "❌ 编译失败！"
    exit 1
  fi
fi

# 启动应用
echo "🚀 正在启动应用..."
$JAVA_17_HOME/bin/java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev

echo "=========================================="

