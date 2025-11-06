package com.bank.workflow.domain.permission.gateway;

import com.bank.workflow.domain.permission.entity.Permission;

import java.util.List;

/**
 * 权限网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface PermissionGateway {
    
    /**
     * 创建权限
     *
     * @param permission 权限实体
     * @return 权限ID
     */
    Long createPermission(Permission permission);
    
    /**
     * 更新权限
     *
     * @param permission 权限实体
     */
    void updatePermission(Permission permission);
    
    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     */
    void deletePermission(Long permissionId);
    
    /**
     * 根据ID查询权限
     *
     * @param permissionId 权限ID
     * @return 权限实体
     */
    Permission getPermissionById(Long permissionId);
    
    /**
     * 根据编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限实体
     */
    Permission getPermissionByCode(String permissionCode);
    
    /**
     * 查询所有权限（平铺列表）
     *
     * @return 权限列表
     */
    List<Permission> listAllPermissions();
    
    /**
     * 根据类型查询权限
     *
     * @param permissionType 权限类型
     * @return 权限列表
     */
    List<Permission> listPermissionsByType(String permissionType);
    
    /**
     * 查询指定父权限下的子权限
     *
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<Permission> listChildrenPermissions(Long parentId);
    
    /**
     * 查询权限树（树形结构）
     *
     * @return 权限树
     */
    List<Permission> getPermissionTree();
    
    /**
     * 批量查询权限详情
     *
     * @param permissionIds 权限ID列表
     * @return 权限列表
     */
    List<Permission> listPermissionsByIds(List<Long> permissionIds);
}

