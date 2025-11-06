#!/bin/bash
# 快速修复数据库脚本

echo "🔧 开始修复数据库..."

# 1. 清空并重建数据库
echo "📁 重建数据库 workflow..."
mysql -u root -p1qaz2wsx -e "DROP DATABASE IF EXISTS workflow; CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ 数据库重建成功！"
else
    echo "❌ 数据库重建失败，请检查 MySQL 连接"
    exit 1
fi

# 2. 验证数据库
echo "🔍 验证数据库..."
mysql -u root -p1qaz2wsx -e "SHOW DATABASES LIKE 'workflow';" 2>/dev/null

echo ""
echo "✅ 数据库修复完成！"
echo ""
echo "📝 下一步："
echo "   cd /Users/yanchao/IdeaProjects/workFolw/backend"
echo "   mvn spring-boot:run"
echo ""

