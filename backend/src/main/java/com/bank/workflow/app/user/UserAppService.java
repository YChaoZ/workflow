package com.bank.workflow.app.user;

import com.bank.workflow.app.user.command.AssignRolesCmd;
import com.bank.workflow.domain.user.entity.User;
import com.bank.workflow.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}

