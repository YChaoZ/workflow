# 数据库迁移脚本说明

## 📋 概述

本目录包含 Flyway 数据库迁移脚本，用于自动初始化工作流系统的数据库表结构和初始数据。

## 📂 脚本文件

### V1__init_tables.sql
**版本**: V1  
**说明**: 创建业务表结构

包含以下业务表：
1. **流程分类表** (`wf_process_category`) - 管理流程分类
2. **流程定义扩展表** (`wf_process_definition_ext`) - 流程定义的扩展信息
3. **流程实例扩展表** (`wf_process_instance_ext`) - 流程实例的扩展信息
4. **任务扩展表** (`wf_task_ext`) - 任务的扩展信息
5. **表单定义表** (`wf_form_definition`) - 表单定义
6. **表单字段表** (`wf_form_field`) - 表单字段配置
7. **表单实例表** (`wf_form_instance`) - 表单实例数据
8. **组织架构表** (`sys_org`) - 组织架构
9. **用户表** (`sys_user`) - 系统用户
10. **角色表** (`sys_role`) - 系统角色
11. **用户角色关联表** (`sys_user_role`) - 用户角色关系
12. **权限表** (`sys_permission`) - 权限配置
13. **角色权限关联表** (`sys_role_permission`) - 角色权限关系
14. **流程监控日志表** (`wf_process_monitor_log`) - 流程监控日志
15. **流程性能统计表** (`wf_process_performance_stat`) - 流程性能统计

**注意**: Flowable 引擎的表（约 70 张表）会由 Flowable 自动创建，无需手动创建。

### V2__init_data.sql
**版本**: V2  
**说明**: 初始化基础数据

包含：
- 默认流程分类（办公流程、财务流程、人事流程等）
- 系统管理员用户（admin/admin123）
- 默认角色（系统管理员、流程管理员、普通用户）
- 默认权限配置
- 角色权限关联

## 🚀 使用方式

### 方式一：自动迁移（推荐）

项目已经配置了 Flyway 自动迁移，只需：

1. **配置数据库连接**
   
   编辑 `application-dev.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/workflow_db?serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4&useSSL=false&allowPublicKeyRetrieval=true
       username: root
       password: your_password
   ```

2. **启动应用**
   
   ```bash
   cd backend/workflow-start
   mvn spring-boot:run
   ```
   
   Flyway 会自动：
   - 检查数据库是否存在
   - 执行未运行过的迁移脚本
   - 记录迁移历史到 `flyway_schema_history` 表

### 方式二：手动执行（可选）

如果需要手动初始化数据库：

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS workflow_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 执行迁移脚本
cd backend/workflow-start/src/main/resources/db/migration
mysql -u root -p workflow_db < V1__init_tables.sql
mysql -u root -p workflow_db < V2__init_data.sql
```

## 📊 数据库表结构

### 业务表（15张）
```
wf_process_category          - 流程分类
wf_process_definition_ext    - 流程定义扩展
wf_process_instance_ext      - 流程实例扩展
wf_task_ext                  - 任务扩展
wf_form_definition           - 表单定义
wf_form_field                - 表单字段
wf_form_instance             - 表单实例
sys_org                      - 组织架构
sys_user                     - 用户
sys_role                     - 角色
sys_user_role                - 用户角色关联
sys_permission               - 权限
sys_role_permission          - 角色权限关联
wf_process_monitor_log       - 流程监控日志
wf_process_performance_stat  - 流程性能统计
```

### Flowable 引擎表（约 70 张，自动创建）
```
ACT_RE_*   - 流程定义和资源（Repository）
ACT_RU_*   - 运行时数据（Runtime）
ACT_HI_*   - 历史数据（History）
ACT_ID_*   - 身份数据（Identity）
ACT_GE_*   - 通用数据（General）
FLW_*      - Flowable 相关表
```

## 🔍 验证迁移

启动应用后，检查迁移历史：

```sql
-- 查看 Flyway 迁移历史
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- 查看业务表
SHOW TABLES LIKE 'wf_%';
SHOW TABLES LIKE 'sys_%';

-- 查看 Flowable 表
SHOW TABLES LIKE 'ACT_%';
SHOW TABLES LIKE 'FLW_%';

-- 验证初始数据
SELECT * FROM sys_user;
SELECT * FROM sys_role;
SELECT * FROM wf_process_category;
```

## ⚠️ 注意事项

1. **脚本命名规则**
   - 格式：`V{版本号}__{描述}.sql`
   - 版本号必须唯一且递增
   - 描述使用下划线分隔单词
   - 示例：`V3__add_comment_table.sql`

2. **脚本幂等性**
   - 所有 CREATE 语句都使用 `IF NOT EXISTS`
   - 所有 INSERT 语句都使用 `INSERT IGNORE`
   - 确保脚本可以多次执行而不出错

3. **字符集编码**
   - 数据库：`utf8mb4`
   - 排序规则：`utf8mb4_unicode_ci`
   - 支持 Emoji 和中文

4. **Flyway 配置**
   ```yaml
   spring:
     flyway:
       enabled: true                    # 启用 Flyway
       locations: classpath:db/migration # 脚本位置
       baseline-on-migrate: true        # 基线迁移
       validate-on-migrate: true        # 验证脚本
   ```

5. **生产环境注意**
   - 首次部署前备份数据库
   - 在测试环境验证迁移脚本
   - 检查脚本执行时间和锁表情况
   - 大表变更考虑在线 DDL

## 📝 添加新迁移

当需要修改数据库结构时：

1. 创建新的迁移脚本：`V3__your_description.sql`
2. 编写 DDL/DML 语句
3. 测试脚本的幂等性
4. 提交代码后，重启应用自动执行

示例：
```sql
-- V3__add_comment_table.sql
CREATE TABLE IF NOT EXISTS wf_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_instance_id VARCHAR(64) NOT NULL,
    task_id VARCHAR(64),
    user_id VARCHAR(64) NOT NULL,
    comment_text TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程评论表';
```

## 🔗 相关文档

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Flowable 数据库表说明](https://www.flowable.com/open-source/docs/bpmn/ch03-API#database-tables)
- [项目快速启动指南](../../../../../QUICK_START.md)

## 👥 默认用户

系统初始化后提供以下测试用户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 系统管理员 | 拥有所有权限 |

**⚠️ 生产环境请立即修改默认密码！**

