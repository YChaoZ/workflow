package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.user.entity.User;
import com.bank.workflow.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户网关实现（基于JDBC）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {
    
    private final JdbcTemplate jdbcTemplate;
    
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
                        "email, mobile, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            
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
}

