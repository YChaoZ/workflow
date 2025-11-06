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
    
    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, java.util.List<Long> roleIds);
    
    /**
     * 查询用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    java.util.List<Long> listRoleIdsByUserId(Long userId);
    
    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    java.util.List<User> listAllUsers();
    
    /**
     * 统计使用该角色的用户数量
     *
     * @param roleId 角色ID
     * @return 用户数量
     */
    long countUsersByRoleId(Long roleId);
    
    /**
     * 统计该部门的用户数量
     *
     * @param deptId 部门ID
     * @return 用户数量
     */
    long countUsersByDeptId(Long deptId);
    
    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
    
    /**
     * 分页查询用户
     *
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param departmentId 部门ID（可选）
     * @param status 状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    com.bank.workflow.app.dto.PageResult<User> pageUsers(String username, String realName, Long departmentId, Boolean status, Integer page, Integer size);
}

