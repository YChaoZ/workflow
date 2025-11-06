#!/bin/bash

# ========================================
# 工作流系统一键启动脚本
# ========================================

clear
echo "🚀 工作流系统启动向导"
echo "=========================================="
echo ""

# 检查jar包
if [ ! -f "target/workflow-system-1.0.0-SNAPSHOT.jar" ]; then
    echo "❌ jar包不存在，请先编译项目"
    exit 1
fi

echo "✅ jar包已准备好"
echo ""
echo "=========================================="
echo "⚠️  重要提示"
echo "=========================================="
echo ""
echo "在启动之前，请确保已重置数据库！"
echo ""
echo "执行命令："
echo "  mysql -u root -p < $(pwd)/重置数据库.sql"
echo ""
echo "或者手动执行："
echo "  DROP DATABASE IF EXISTS workflow;"
echo "  CREATE DATABASE workflow CHARACTER SET utf8mb4;"
echo ""
echo "=========================================="
echo ""

read -p "✋ 已经重置数据库了吗？(y/n): " confirm

if [[ $confirm != [yY] && $confirm != [yY][eE][sS] ]]; then
    echo ""
    echo "❌ 请先重置数据库，然后重新运行此脚本"
    echo ""
    echo "执行命令："
    echo "  mysql -u root -p < $(pwd)/重置数据库.sql"
    echo ""
    exit 1
fi

echo ""
echo "=========================================="
echo "🚀 正在启动服务..."
echo "=========================================="
echo ""
echo "提示："
echo "  - 按 Ctrl+C 可停止服务"
echo "  - 启动成功后访问: http://localhost:9099"
echo "  - 接口文档: http://localhost:9099/doc.html"
echo ""
echo "=========================================="
echo ""

# 启动服务
java -jar target/workflow-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev

