package com.bank.workflow.app.permission;

import com.bank.workflow.app.permission.command.CreatePermissionCmd;
import com.bank.workflow.app.permission.command.UpdatePermissionCmd;
import com.bank.workflow.domain.permission.entity.Permission;
import com.bank.workflow.domain.permission.gateway.PermissionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAppService {
    
    private final PermissionGateway permissionGateway;
    
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(CreatePermissionCmd cmd) {
        log.info("接收创建权限请求: cmd={}", cmd);
        
        Permission existingPerm = permissionGateway.getPermissionByCode(cmd.getPermissionCode());
        if (existingPerm != null) {
            throw new IllegalArgumentException("权限编码已存在: " + cmd.getPermissionCode());
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(cmd, permission);
        
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        
        Long permissionId = permissionGateway.createPermission(permission);
        
        log.info("权限创建成功: permissionId={}", permissionId);
        return permissionId;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(UpdatePermissionCmd cmd) {
        log.info("接收更新权限请求: cmd={}", cmd);
        
        Permission existingPerm = permissionGateway.getPermissionById(cmd.getId());
        if (existingPerm == null) {
            throw new IllegalArgumentException("权限不存在: " + cmd.getId());
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(cmd, permission);
        
        permissionGateway.updatePermission(permission);
        
        log.info("权限更新成功: permissionId={}", cmd.getId());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        log.info("接收删除权限请求: permissionId={}", permissionId);
        
        Permission permission = permissionGateway.getPermissionById(permissionId);
        if (permission == null) {
            throw new IllegalArgumentException("权限不存在: " + permissionId);
        }
        
        permissionGateway.deletePermission(permissionId);
        
        log.info("权限删除成功: permissionId={}", permissionId);
    }
    
    public Permission getPermissionById(Long permissionId) {
        log.info("接收查询权限详情请求: permissionId={}", permissionId);
        return permissionGateway.getPermissionById(permissionId);
    }
    
    public List<Permission> listAllPermissions() {
        log.info("接收查询所有权限请求");
        return permissionGateway.listAllPermissions();
    }
    
    public List<Permission> getPermissionTree() {
        log.info("接收查询权限树请求");
        return permissionGateway.getPermissionTree();
    }
    
    public List<Permission> listPermissionsByType(String permissionType) {
        log.info("接收根据类型查询权限请求: permissionType={}", permissionType);
        return permissionGateway.listPermissionsByType(permissionType);
    }
}

