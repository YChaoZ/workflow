package com.bank.workflow.app.role;

import com.bank.workflow.app.role.command.AssignPermissionsCmd;
import com.bank.workflow.app.role.command.CreateRoleCmd;
import com.bank.workflow.app.role.command.UpdateRoleCmd;
import com.bank.workflow.domain.role.entity.Role;
import com.bank.workflow.domain.role.gateway.RoleGateway;
import com.bank.workflow.domain.user.gateway.UserGateway;
import com.bank.workflow.infrastructure.redis.PermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleAppService {
    
    private final RoleGateway roleGateway;
    private final UserGateway userGateway;
    private final PermissionCacheService permissionCacheService;
    
    /**
     * 创建角色
     *
     * @param cmd 创建命令
     * @return 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(CreateRoleCmd cmd) {
        log.info("接收创建角色请求: cmd={}", cmd);
        
        // 检查角色编码是否已存在
        Role existingRole = roleGateway.getRoleByCode(cmd.getRoleCode());
        if (existingRole != null) {
            throw new IllegalArgumentException("角色编码已存在: " + cmd.getRoleCode());
        }
        
        // 构建角色实体
        Role role = new Role();
        BeanUtils.copyProperties(cmd, role);
        
        // 默认为自定义角色
        if (role.getRoleType() == null || role.getRoleType().isEmpty()) {
            role.setRoleType("CUSTOM");
        }
        
        // 默认启用
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        
        // 保存角色
        Long roleId = roleGateway.createRole(role);
        
        // 分配权限
        if (cmd.getPermissionIds() != null && !cmd.getPermissionIds().isEmpty()) {
            roleGateway.assignPermissions(roleId, cmd.getPermissionIds());
        }
        
        log.info("角色创建成功: roleId={}", roleId);
        return roleId;
    }
    
    /**
     * 更新角色
     *
     * @param cmd 更新命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(UpdateRoleCmd cmd) {
        log.info("接收更新角色请求: cmd={}", cmd);
        
        // 检查角色是否存在
        Role existingRole = roleGateway.getRoleById(cmd.getId());
        if (existingRole == null) {
            throw new IllegalArgumentException("角色不存在: " + cmd.getId());
        }
        
        // 系统角色不允许修改类型
        if (existingRole.isSystemRole() && !"SYSTEM".equals(cmd.getRoleType())) {
            throw new IllegalArgumentException("系统角色不允许修改类型");
        }
        
        // 检查角色编码是否被其他角色占用
        Role roleByCode = roleGateway.getRoleByCode(cmd.getRoleCode());
        if (roleByCode != null && !roleByCode.getId().equals(cmd.getId())) {
            throw new IllegalArgumentException("角色编码已被占用: " + cmd.getRoleCode());
        }
        
        // 更新角色实体
        Role role = new Role();
        BeanUtils.copyProperties(cmd, role);
        
        roleGateway.updateRole(role);
        
        // 更新权限（如果提供）
        if (cmd.getPermissionIds() != null) {
            roleGateway.assignPermissions(cmd.getId(), cmd.getPermissionIds());
        }
        
        log.info("角色更新成功: roleId={}", cmd.getId());
    }
    
    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        log.info("接收删除角色请求: roleId={}", roleId);
        
        // 检查角色是否存在
        Role role = roleGateway.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在: " + roleId);
        }
        
        // 系统角色不允许删除
        if (role.isSystemRole()) {
            throw new IllegalArgumentException("系统角色不允许删除");
        }
        
        // 检查是否有用户关联
        long userCount = userGateway.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new IllegalArgumentException("该角色已分配给" + userCount + "个用户，无法删除。请先解除用户关联。");
        }
        
        roleGateway.deleteRole(roleId);
        
        log.info("角色删除成功: roleId={}", roleId);
    }
    
    /**
     * 查询角色详情
     *
     * @param roleId 角色ID
     * @return 角色实体（包含权限列表）
     */
    public Role getRoleById(Long roleId) {
        log.info("接收查询角色详情请求: roleId={}", roleId);
        return roleGateway.getRoleById(roleId);
    }
    
    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<Role> listAllRoles() {
        log.info("接收查询所有角色请求");
        return roleGateway.listAllRoles();
    }
    
    /**
     * 根据类型查询角色
     *
     * @param roleType 角色类型
     * @return 角色列表
     */
    public List<Role> listRolesByType(String roleType) {
        log.info("接收根据类型查询角色请求: roleType={}", roleType);
        return roleGateway.listRolesByType(roleType);
    }
    
    /**
     * 为角色分配权限
     *
     * @param cmd 分配权限命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(AssignPermissionsCmd cmd) {
        log.info("接收分配权限请求: cmd={}", cmd);
        
        // 检查角色是否存在
        Role role = roleGateway.getRoleById(cmd.getRoleId());
        if (role == null) {
            throw new IllegalArgumentException("角色不存在: " + cmd.getRoleId());
        }
        
        // TODO: 检查权限是否存在
        
        roleGateway.assignPermissions(cmd.getRoleId(), cmd.getPermissionIds());
        
        // 清除角色权限缓存
        permissionCacheService.clearRolePermissionCache(cmd.getRoleId());
        
        log.info("权限分配成功: roleId={}, 分配{}个权限", cmd.getRoleId(), cmd.getPermissionIds().size());
    }
    
    /**
     * 查询角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        log.info("接收查询角色权限请求: roleId={}", roleId);
        return roleGateway.listPermissionIdsByRoleId(roleId);
    }
}

