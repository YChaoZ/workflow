package com.bank.workflow.app.user;

import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.user.command.AssignRolesCmd;
import com.bank.workflow.app.user.command.CreateUserCmd;
import com.bank.workflow.app.user.command.UpdateUserCmd;
import com.bank.workflow.app.user.query.UserPageQuery;
import com.bank.workflow.domain.user.entity.User;
import com.bank.workflow.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAppService {
    
    private final UserGateway userGateway;
    
    public User getUserById(Long userId) {
        log.info("接收查询用户详情请求: userId={}", userId);
        User user = userGateway.findById(userId);
        if (user != null) {
            user.setRoleIds(userGateway.listRoleIdsByUserId(userId));
        }
        return user;
    }
    
    public List<User> listAllUsers() {
        log.info("接收查询所有用户请求");
        return userGateway.listAllUsers();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(AssignRolesCmd cmd) {
        log.info("接收为用户分配角色请求: cmd={}", cmd);
        
        User user = userGateway.findById(cmd.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + cmd.getUserId());
        }
        
        userGateway.assignRoles(cmd.getUserId(), cmd.getRoleIds());
        
        log.info("角色分配成功: userId={}, 分配{}个角色", cmd.getUserId(), cmd.getRoleIds().size());
    }
    
    public List<Long> listRoleIdsByUserId(Long userId) {
        log.info("接收查询用户角色请求: userId={}", userId);
        return userGateway.listRoleIdsByUserId(userId);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(CreateUserCmd cmd) {
        log.info("接收创建用户请求: username={}", cmd.getUsername());
        
        // 检查用户名是否已存在
        User existingUser = userGateway.findByUsername(cmd.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("用户名已存在: " + cmd.getUsername());
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(cmd.getUsername());
        user.setRealName(cmd.getRealName());
        user.setEmail(cmd.getEmail());
        user.setMobile(cmd.getPhone());
        user.setDeptId(cmd.getDepartmentId());
        user.setStatus(cmd.getStatus() ? 1 : 0);
        
        // 密码编码（Base64）
        // TODO: 在生产环境中应使用BCrypt或其他安全的密码加密算法
        String encodedPassword = Base64.getEncoder().encodeToString(cmd.getPassword().getBytes());
        user.setPassword(encodedPassword);
        
        // 保存用户
        Long userId = userGateway.saveUser(user);
        
        // 分配角色
        if (cmd.getRoleIds() != null && !cmd.getRoleIds().isEmpty()) {
            userGateway.assignRoles(userId, cmd.getRoleIds());
        }
        
        log.info("用户创建成功: userId={}, username={}", userId, cmd.getUsername());
        return userId;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UpdateUserCmd cmd) {
        log.info("接收更新用户请求: userId={}", cmd.getId());
        
        // 检查用户是否存在
        User user = userGateway.findById(cmd.getId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + cmd.getId());
        }
        
        // 更新用户信息
        if (cmd.getRealName() != null) {
            user.setRealName(cmd.getRealName());
        }
        if (cmd.getEmail() != null) {
            user.setEmail(cmd.getEmail());
        }
        if (cmd.getPhone() != null) {
            user.setMobile(cmd.getPhone());
        }
        if (cmd.getDepartmentId() != null) {
            user.setDeptId(cmd.getDepartmentId());
        }
        if (cmd.getStatus() != null) {
            user.setStatus(cmd.getStatus() ? 1 : 0);
        }
        
        userGateway.updateUser(user);
        
        // 更新角色
        if (cmd.getRoleIds() != null) {
            userGateway.assignRoles(cmd.getId(), cmd.getRoleIds());
        }
        
        log.info("用户更新成功: userId={}", cmd.getId());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        log.info("接收删除用户请求: userId={}", userId);
        
        // 检查用户是否存在
        User user = userGateway.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }
        
        userGateway.deleteUser(userId);
        
        log.info("用户删除成功: userId={}", userId);
    }
    
    public PageResult<User> pageUsers(UserPageQuery query) {
        log.info("接收分页查询用户请求: query={}", query);
        
        return userGateway.pageUsers(
                query.getUsername(),
                query.getRealName(),
                query.getDepartmentId(),
                query.getStatus(),
                query.getPage(),
                query.getSize()
        );
    }
}

