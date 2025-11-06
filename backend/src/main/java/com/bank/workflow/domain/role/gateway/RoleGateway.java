package com.bank.workflow.domain.role.gateway;

import com.bank.workflow.domain.role.entity.Role;

import java.util.List;

/**
 * 角色网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface RoleGateway {
    
    /**
     * 创建角色
     *
     * @param role 角色实体
     * @return 角色ID
     */
    Long createRole(Role role);
    
    /**
     * 更新角色
     *
     * @param role 角色实体
     */
    void updateRole(Role role);
    
    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);
    
    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色实体
     */
    Role getRoleById(Long roleId);
    
    /**
     * 根据编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色实体
     */
    Role getRoleByCode(String roleCode);
    
    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> listAllRoles();
    
    /**
     * 根据类型查询角色
     *
     * @param roleType 角色类型
     * @return 角色列表
     */
    List<Role> listRolesByType(String roleType);
    
    /**
     * 为角色分配权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 查询角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> listPermissionIdsByRoleId(Long roleId);
    
    /**
     * 批量查询角色的权限
     *
     * @param roleIds 角色ID列表
     * @return 权限ID列表（去重）
     */
    List<Long> listPermissionIdsByRoleIds(List<Long> roleIds);
}

