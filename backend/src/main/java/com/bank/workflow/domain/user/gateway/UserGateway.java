package com.bank.workflow.domain.user.gateway;

import com.bank.workflow.domain.user.entity.User;

/**
 * 用户网关接口
 * 由 Infrastructure 层实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface UserGateway {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);
    
    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    User findById(Long userId);
    
    /**
     * 保存用户
     *
     * @param user 用户
     * @return 用户ID
     */
    Long saveUser(User user);
    
    /**
     * 更新用户
     *
     * @param user 用户
     */
    void updateUser(User user);
}

