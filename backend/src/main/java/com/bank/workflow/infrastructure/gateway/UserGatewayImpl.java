package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.user.entity.User;
import com.bank.workflow.domain.user.gateway.UserGateway;
import com.bank.workflow.infrastructure.persistence.mapper.UserRoleMapper;
import com.bank.workflow.infrastructure.persistence.po.UserRoleDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户网关实现（基于JDBC + MyBatis-Plus）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {
    
    private final JdbcTemplate jdbcTemplate;
    private final UserRoleMapper userRoleMapper;
    
    @Override
    public User findByUsername(String username) {
        log.info("根据用户名查询用户: username={}", username);
        
        try {
            String sql = "SELECT id, username, real_name, password, dept_id, " +
                        "position, email, mobile, status FROM sys_user WHERE username = ? AND status = 1";
            
            List<User> users = jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(User.class), 
                username);
            
            if (users.isEmpty()) {
                log.warn("用户不存在: username={}", username);
                return null;
            }
            
            return users.get(0);
        } catch (Exception e) {
            log.error("查询用户失败: username={}, error={}", username, e.getMessage(), e);
            throw new RuntimeException("查询用户失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public User findById(Long userId) {
        log.info("根据用户ID查询用户: userId={}", userId);
        
        try {
            String sql = "SELECT id, username, real_name, password, dept_id, " +
                        "position, email, mobile, status FROM sys_user WHERE id = ? AND status = 1";
            
            List<User> users = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(User.class),
                userId);
            
            if (users.isEmpty()) {
                log.warn("用户不存在: userId={}", userId);
                return null;
            }
            
            return users.get(0);
        } catch (Exception e) {
            log.error("查询用户失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("查询用户失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long saveUser(User user) {
        log.info("保存用户: username={}", user.getUsername());
        
        try {
            String sql = "INSERT INTO sys_user (username, real_name, password, dept_id, position, " +
                        "email, mobile, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            
            jdbcTemplate.update(sql,
                user.getUsername(),
                user.getRealName(),
                user.getPassword(),
                user.getDeptId(),
                user.getPosition(),
                user.getEmail(),
                user.getMobile(),
                user.getStatus());
            
            // 获取刚插入的用户ID
            String querySql = "SELECT LAST_INSERT_ID()";
            Long userId = jdbcTemplate.queryForObject(querySql, Long.class);
            
            log.info("用户保存成功: userId={}", userId);
            return userId;
        } catch (Exception e) {
            log.error("保存用户失败: username={}, error={}", user.getUsername(), e.getMessage(), e);
            throw new RuntimeException("保存用户失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateUser(User user) {
        log.info("更新用户: userId={}", user.getUserId());
        
        try {
            String sql = "UPDATE sys_user SET real_name = ?, dept_id = ?, position = ?, " +
                        "email = ?, mobile = ?, status = ?, updated_at = NOW() WHERE id = ?";
            
            jdbcTemplate.update(sql,
                user.getRealName(),
                user.getDeptId(),
                user.getPosition(),
                user.getEmail(),
                user.getMobile(),
                user.getStatus(),
                user.getUserId());
            
            log.info("用户更新成功: userId={}", user.getUserId());
        } catch (Exception e) {
            log.error("更新用户失败: userId={}, error={}", user.getUserId(), e.getMessage(), e);
            throw new RuntimeException("更新用户失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        log.info("为用户分配角色: userId={}, roleIds={}", userId, roleIds);
        
        // 删除现有角色关联
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);
        
        // 插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            Date now = new Date();
            for (Long roleId : roleIds) {
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(userId);
                userRoleDO.setRoleId(roleId);
                userRoleDO.setCreateTime(now);
                userRoleMapper.insert(userRoleDO);
            }
        }
        
        log.info("角色分配成功: userId={}, 分配{}个角色", userId, roleIds == null ? 0 : roleIds.size());
    }
    
    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        log.info("查询用户角色: userId={}", userId);
        
        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleDO::getUserId, userId);
        
        List<UserRoleDO> list = userRoleMapper.selectList(wrapper);
        
        List<Long> roleIds = list.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());
        
        log.info("用户拥有{}个角色", roleIds.size());
        return roleIds;
    }
    
    @Override
    public List<User> listAllUsers() {
        log.info("查询所有用户");
        
        try {
            String sql = "SELECT id, username, real_name, password, dept_id, " +
                        "position, email, mobile, status, create_time, update_time FROM sys_user WHERE deleted = 0";
            
            List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
            
            // 查询每个用户的角色
            for (User user : users) {
                user.setRoleIds(listRoleIdsByUserId(user.getId()));
            }
            
            log.info("查询到{}个用户", users.size());
            return users;
        } catch (Exception e) {
            log.error("查询所有用户失败: error={}", e.getMessage(), e);
            throw new RuntimeException("查询所有用户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 统计使用该角色的用户数量
     */
    @Override
    public long countUsersByRoleId(Long roleId) {
        log.info("统计使用角色的用户数量: roleId={}", roleId);
        
        try {
            String sql = "SELECT COUNT(*) FROM sys_user_role WHERE role_id = ?";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, roleId);
            log.info("角色{}被{}个用户使用", roleId, count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计角色用户数量失败: roleId={}, error={}", roleId, e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 统计该部门的用户数量
     */
    @Override
    public long countUsersByDeptId(Long deptId) {
        log.info("统计部门的用户数量: deptId={}", deptId);
        
        try {
            String sql = "SELECT COUNT(*) FROM sys_user WHERE dept_id = ? AND deleted = 0";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, deptId);
            log.info("部门{}有{}个用户", deptId, count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计部门用户数量失败: deptId={}, error={}", deptId, e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        log.info("删除用户: userId={}", userId);
        
        try {
            // 逻辑删除用户
            String sql = "UPDATE sys_user SET deleted = 1, status = 0 WHERE id = ?";
            int rows = jdbcTemplate.update(sql, userId);
            
            if (rows > 0) {
                // 删除用户角色关联
                LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserRoleDO::getUserId, userId);
                userRoleMapper.delete(wrapper);
                
                log.info("用户删除成功: userId={}", userId);
            } else {
                log.warn("用户不存在或已删除: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("删除用户失败: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("删除用户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 分页查询用户
     */
    @Override
    public com.bank.workflow.app.dto.PageResult<User> pageUsers(String username, String realName, 
                                                                  Long departmentId, Boolean status, 
                                                                  Integer page, Integer size) {
        log.info("分页查询用户: username={}, realName={}, departmentId={}, status={}, page={}, size={}", 
                username, realName, departmentId, status, page, size);
        
        try {
            // 构建查询条件
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM sys_user WHERE deleted = 0");
            StringBuilder querySql = new StringBuilder("SELECT id, username, real_name, password, dept_id, position, email, mobile, status, create_time, update_time FROM sys_user WHERE deleted = 0");
            List<Object> params = new java.util.ArrayList<>();
            
            if (username != null && !username.trim().isEmpty()) {
                countSql.append(" AND username LIKE ?");
                querySql.append(" AND username LIKE ?");
                params.add("%" + username + "%");
            }
            
            if (realName != null && !realName.trim().isEmpty()) {
                countSql.append(" AND real_name LIKE ?");
                querySql.append(" AND real_name LIKE ?");
                params.add("%" + realName + "%");
            }
            
            if (departmentId != null) {
                countSql.append(" AND dept_id = ?");
                querySql.append(" AND dept_id = ?");
                params.add(departmentId);
            }
            
            if (status != null) {
                countSql.append(" AND status = ?");
                querySql.append(" AND status = ?");
                params.add(status ? 1 : 0);
            }
            
            // 查询总数
            Long total = jdbcTemplate.queryForObject(countSql.toString(), Long.class, params.toArray());
            if (total == null) {
                total = 0L;
            }
            
            // 分页查询
            querySql.append(" ORDER BY create_time DESC LIMIT ? OFFSET ?");
            params.add(size);
            params.add((page - 1) * size);
            
            List<User> users = jdbcTemplate.query(querySql.toString(),
                    new BeanPropertyRowMapper<>(User.class),
                    params.toArray());
            
            // 为每个用户加载角色ID列表
            for (User user : users) {
                user.setRoleIds(listRoleIdsByUserId(user.getId()));
            }
            
            log.info("分页查询用户成功: total={}, page={}, size={}, 返回{}条记录", total, page, size, users.size());
            
            return new com.bank.workflow.app.dto.PageResult<User>(total, users);
        } catch (Exception e) {
            log.error("分页查询用户失败: error={}", e.getMessage(), e);
            throw new RuntimeException("分页查询用户失败: " + e.getMessage(), e);
        }
    }
}

