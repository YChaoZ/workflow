# 🔧 Flyway 启动异常解决方案

## 问题描述

启动时报错：
```
org.flywaydb.core.api.FlywayException: 
Found non-empty schema(s) `workflow` but no schema history table. 
Use baseline() or set baselineOnMigrate to true to initialize the schema history table.
```

## 问题原因

数据库 `workflow` 不是空的（可能包含一些表），但是没有 Flyway 的历史记录表 `flyway_schema_history`，Flyway 不知道数据库的当前状态，不敢执行迁移。

---

## 🎯 解决方案一：配置自动基线（推荐）✅

### 已自动修复！

在 `application.yml` 中添加了 Flyway 配置：

```yaml
spring:
  flyway:
    enabled: true                # 启用 Flyway
    baseline-on-migrate: true    # 在非空数据库上自动创建基线
    baseline-version: 0          # 基线版本号
    validate-on-migrate: true    # 迁移前验证
```

**这个配置的作用**：
- ✅ 允许 Flyway 在非空数据库上运行
- ✅ 自动创建 `flyway_schema_history` 表
- ✅ 记录数据库的初始状态为版本 0
- ✅ 然后正常执行 V1、V2 等迁移脚本

### 现在重新启动

```bash
cd backend
mvn spring-boot:run
```

应该能正常启动了！

---

## 🎯 解决方案二：清空数据库（彻底）

如果方案一不行，可以清空数据库重新初始化：

### 步骤 1：清空数据库
```sql
-- 登录 MySQL
mysql -u root -p1qaz2wsx

-- 删除并重建数据库
DROP DATABASE workflow;
CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 退出
EXIT;
```

### 步骤 2：删除配置中的 Flyway 设置
如果清空了数据库，可以移除 `baseline-on-migrate` 配置：
```yaml
spring:
  flyway:
    enabled: true
    # baseline-on-migrate: true  # 清空数据库后不需要
```

### 步骤 3：重新启动
```bash
cd backend
mvn spring-boot:run
```

Flyway 会在空数据库上正常执行 V1 和 V2 脚本。

---

## 🎯 解决方案三：手动创建基线（高级）

### 使用 MySQL 命令
```bash
cd backend/src/main/resources/db/migration

# 如果数据库有表，先手动创建 Flyway 历史表
mysql -u root -p1qaz2wsx workflow << EOF
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INT NOT NULL,
    version VARCHAR(50),
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INT,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INT NOT NULL,
    success BOOLEAN NOT NULL,
    PRIMARY KEY (installed_rank)
);

INSERT INTO flyway_schema_history (
    installed_rank, version, description, type, script, 
    checksum, installed_by, execution_time, success
) VALUES (
    1, '0', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', 
    NULL, 'manual', 0, 1
);
EOF
```

然后重新启动应用。

---

## ✅ 验证修复成功

### 1. 观察启动日志

**成功的标志**：
```
✓ Flyway - Database: jdbc:mysql://localhost:3306/workflow
✓ Flyway - Successfully validated 2 migrations
✓ Flyway - Creating Schema History table `workflow`.`flyway_schema_history`
✓ Flyway - Current version of schema `workflow`: 0
✓ Flyway - Migrating schema `workflow` to version "1 - init tables"
✓ Flyway - Migrating schema `workflow` to version "2 - init data"
✓ Flyway - Successfully applied 2 migrations
✓ Flowable ProcessEngine initialized
✓ Started WorkFlowApplication in X seconds
```

### 2. 检查数据库

```sql
-- 连接数据库
mysql -u root -p1qaz2wsx workflow

-- 查看 Flyway 历史表
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- 应该看到 3 条记录：
-- 1. Version 0 (基线)
-- 2. Version 1 (init_tables)
-- 3. Version 2 (init_data)

-- 查看所有表（应该有约 85 张）
SHOW TABLES;

-- 验证业务表
SELECT * FROM sys_user;
SELECT * FROM sys_role;
SELECT * FROM wf_process_category;
```

---

## 🐛 其他可能的问题

### 问题 1：Flyway 脚本执行失败

**错误**：`Migration V1__init_tables.sql failed`

**原因**：SQL 脚本语法错误或权限不足

**解决**：
```sql
-- 检查用户权限
SHOW GRANTS FOR 'root'@'localhost';

-- 如果权限不足，授权
GRANT ALL PRIVILEGES ON workflow.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 问题 2：字符集不匹配

**错误**：字符集相关错误

**解决**：
```sql
-- 检查数据库字符集
SHOW CREATE DATABASE workflow;

-- 应该是 utf8mb4
-- 如果不是，重建数据库
DROP DATABASE workflow;
CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 问题 3：Flyway 版本冲突

**错误**：`FlywayException: Validate failed`

**解决**：
```sql
-- 清空 Flyway 历史
TRUNCATE TABLE flyway_schema_history;

-- 或删除并重新初始化
DROP TABLE flyway_schema_history;
```

---

## 📋 配置说明

### baseline-on-migrate: true 的作用

```yaml
spring:
  flyway:
    baseline-on-migrate: true
```

**什么时候用**：
- ✅ 数据库已经存在一些表
- ✅ 首次引入 Flyway
- ✅ 不想删除现有数据

**作用**：
1. Flyway 检测到非空数据库
2. 自动创建 `flyway_schema_history` 表
3. 插入一条基线记录（版本 0）
4. 然后执行版本 1、2、3... 的迁移脚本

**不用的情况**：
- ❌ 数据库是空的
- ❌ 想要严格控制迁移历史

---

## 🔍 排查步骤

如果还是启动失败，按照以下步骤排查：

### 1. 检查数据库连接
```bash
mysql -u root -p1qaz2wsx -e "SELECT 1"
```

### 2. 检查数据库是否存在
```bash
mysql -u root -p1qaz2wsx -e "SHOW DATABASES LIKE 'workflow'"
```

### 3. 检查数据库内容
```sql
mysql -u root -p1qaz2wsx workflow -e "SHOW TABLES"
```

### 4. 检查 Flyway 历史表
```sql
mysql -u root -p1qaz2wsx workflow -e "SELECT * FROM flyway_schema_history"
```

### 5. 查看详细日志
```bash
cd backend
mvn spring-boot:run -X  # -X 参数显示详细日志
```

---

## 💡 推荐操作

### 如果是新项目（推荐）
```sql
-- 1. 清空数据库
DROP DATABASE workflow;
CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 启动应用（Flyway 自动初始化）
cd backend && mvn spring-boot:run
```

### 如果有重要数据（谨慎）
```yaml
# 保持配置
spring:
  flyway:
    baseline-on-migrate: true
```

然后启动应用，Flyway 会保留现有表并执行新的迁移。

---

## 📖 相关文档

- [Flyway 官方文档 - Baseline](https://flywaydb.org/documentation/command/baseline)
- [Spring Boot Flyway 配置](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.data-migration.spring.flyway)

---

## ✅ 快速修复命令

**最快的解决方案（适合新项目）**：

```bash
# 1. 清空数据库
mysql -u root -p1qaz2wsx -e "DROP DATABASE IF EXISTS workflow; CREATE DATABASE workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 重新启动
cd /Users/yanchao/IdeaProjects/workFolw/backend
mvn spring-boot:run
```

---

**🎉 按照以上步骤，应该能够成功启动！**

