# 🔧 修复Flyway迁移问题

**问题**：Table 'wf_process_category' already exists  
**时间**：2025-11-05  

---

## 📋 问题分析

### 错误信息
```
Error Code : 1050
Message    : Table 'wf_process_category' already exists
```

### 原因
Flyway迁移脚本执行失败，但表已经被创建了，导致：
1. 表存在于数据库中
2. 但Flyway历史表中记录该迁移为失败
3. 再次启动时Flyway尝试重新执行，遇到表已存在错误

---

## ✅ 解决方案

### 方案1：修改SQL脚本（已完成）✅

在所有 `CREATE TABLE` 语句添加 `IF NOT EXISTS`：

```sql
-- 修改前
CREATE TABLE wf_process_category (

-- 修改后
CREATE TABLE IF NOT EXISTS wf_process_category (
```

**修改的文件**：
- ✅ V3__mvp2_tables.sql（3个表全部添加）

---

### 方案2：清理Flyway历史表

**执行以下SQL语句**：

```sql
-- 1. 查看Flyway历史表
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;

-- 2. 删除失败的V3迁移记录
DELETE FROM flyway_schema_history 
WHERE version = '3' AND success = 0;

-- 3. 验证删除
SELECT * FROM flyway_schema_history WHERE version = '3';
```

---

## 🚀 执行步骤

### 步骤1：清理Flyway历史

**在MySQL客户端或IDEA Database工具中执行**：

```sql
USE workflow;

-- 删除失败的迁移记录
DELETE FROM flyway_schema_history WHERE version = '3' AND success = 0;
DELETE FROM flyway_schema_history WHERE version = '4' AND success = 0;

-- 确认清理成功
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC;
```

**预期结果**：
```
+-------+-------------+---------------------+---------+
| version | description | installed_on        | success |
+-------+-------------+---------------------+---------+
| 2       | init data   | 2025-11-05 15:30:00 | 1       |
| 1       | init tables | 2025-11-05 15:30:00 | 1       |
+-------+-------------+---------------------+---------+
```

版本3和4应该不存在。

---

### 步骤2：重新编译后端

```bash
cd /Users/yanchao/IdeaProjects/workFolw/backend
mvn clean compile
```

这样会将修改后的SQL脚本复制到target目录。

---

### 步骤3：重新启动后端

```bash
cd /Users/yanchao/IdeaProjects/workFolw/backend
mvn spring-boot:run
```

**预期日志**：
```
Flyway: Migrating schema `workflow` to version "3 - mvp2 tables"
Flyway: Migrating schema `workflow` to version "4 - mvp2 init data"  
Flyway: Successfully applied 2 migrations
Started WorkFlowApplication in X.XXX seconds
```

---

## 📊 验证结果

### 1. 检查表是否创建成功

```sql
SHOW TABLES LIKE 'wf%';
```

**预期结果**：
```
+---------------------------+
| Tables_in_workflow (wf%)  |
+---------------------------+
| wf_process_category       |
| wf_task_attachment        |
| wf_task_comment           |
| (其他表...)               |
+---------------------------+
```

---

### 2. 检查分类数据

```sql
SELECT id, name, code, parent_id 
FROM wf_process_category 
ORDER BY parent_id, sort_order;
```

**预期结果**：
```
+----+--------------+-----------------+-----------+
| id | name         | code            | parent_id |
+----+--------------+-----------------+-----------+
|  1 | 行政管理     | administration  |         0 |
|  2 | 人事管理     | hr              |         0 |
|  3 | 财务管理     | finance         |         0 |
...
|  6 | 请假审批     | leave           |         1 |
|  7 | 出差申请     | travel          |         1 |
...
（共17条记录）
```

---

### 3. 检查Flyway历史

```sql
SELECT version, description, installed_on, execution_time, success 
FROM flyway_schema_history 
ORDER BY installed_rank;
```

**预期结果**：
```
+---------+-------------+---------------------+----------------+---------+
| version | description | installed_on        | execution_time | success |
+---------+-------------+---------------------+----------------+---------+
| 1       | init tables | 2025-11-05 15:30:00 | 5234          | 1       |
| 2       | init data   | 2025-11-05 15:30:00 | 1523          | 1       |
| 3       | mvp2 tables | 2025-11-05 17:30:00 | 856           | 1       |
| 4       | mvp2 init...| 2025-11-05 17:30:00 | 234           | 1       |
+---------+-------------+---------------------+----------------+---------+
```

所有success列应该都是1。

---

## 💡 预防措施

### 1. 始终使用 IF NOT EXISTS

```sql
-- 好的做法 ✅
CREATE TABLE IF NOT EXISTS table_name (
  ...
);

-- 避免使用 ❌
CREATE TABLE table_name (
  ...
);
```

---

### 2. 迁移脚本幂等性

确保迁移脚本可以安全地多次执行：

```sql
-- 创建表
CREATE TABLE IF NOT EXISTS ...

-- 插入数据（使用 INSERT IGNORE 或检查存在性）
INSERT IGNORE INTO ...

-- 或者
INSERT INTO ... 
ON DUPLICATE KEY UPDATE ...

-- 添加列（检查不存在）
ALTER TABLE table_name 
ADD COLUMN IF NOT EXISTS column_name ...
```

---

### 3. 迁移失败处理

如果迁移失败：
1. 不要删除表
2. 清理Flyway历史表的失败记录
3. 修复SQL脚本
4. 重新运行迁移

---

## 🎯 快速命令

### 一键清理（谨慎使用）

```sql
-- 清理失败的V3和V4迁移
DELETE FROM flyway_schema_history WHERE version IN ('3', '4') AND success = 0;

-- 如果需要完全重置V3和V4
DELETE FROM flyway_schema_history WHERE version IN ('3', '4');
DROP TABLE IF EXISTS wf_process_category;
DROP TABLE IF EXISTS wf_task_comment;
DROP TABLE IF EXISTS wf_task_attachment;
```

---

## ✅ 问题解决清单

- [x] SQL脚本添加 `IF NOT EXISTS`
- [ ] 清理Flyway历史表
- [ ] 重新编译后端
- [ ] 重新启动后端
- [ ] 验证表创建成功
- [ ] 验证数据导入成功

---

## 📞 下一步

1. **执行SQL清理命令**（方案2步骤1）
2. **重新编译**：`mvn clean compile`
3. **重新启动**：`mvn spring-boot:run`
4. **验证成功**：检查日志和数据库

---

**问题已诊断，解决方案已就绪！** 🚀

请执行上面的SQL清理命令，然后重启后端即可！

