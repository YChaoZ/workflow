package com.bank.workflow.adapter.web;

import com.bank.workflow.app.role.RoleAppService;
import com.bank.workflow.app.role.command.AssignPermissionsCmd;
import com.bank.workflow.app.role.command.CreateRoleCmd;
import com.bank.workflow.app.role.command.UpdateRoleCmd;
import com.bank.workflow.domain.role.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleAppService roleAppService;
    
    /**
     * 创建角色
     */
    @PostMapping
    public Map<String, Object> createRole(@Valid @RequestBody CreateRoleCmd cmd) {
        log.info("接收创建角色请求: cmd={}", cmd);
        
        Long roleId = roleAppService.createRole(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", Map.of("roleId", roleId));
        return result;
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    public Map<String, Object> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody UpdateRoleCmd cmd) {
        log.info("接收更新角色请求: roleId={}, cmd={}", roleId, cmd);
        
        cmd.setId(roleId);
        roleAppService.updateRole(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return result;
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    public Map<String, Object> deleteRole(@PathVariable Long roleId) {
        log.info("接收删除角色请求: roleId={}", roleId);
        
        roleAppService.deleteRole(roleId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 查询角色详情
     */
    @GetMapping("/{roleId}")
    public Map<String, Object> getRoleById(@PathVariable Long roleId) {
        log.info("接收查询角色详情请求: roleId={}", roleId);
        
        Role role = roleAppService.getRoleById(roleId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", role);
        return result;
    }
    
    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    public Map<String, Object> listAllRoles() {
        log.info("接收查询所有角色请求");
        
        List<Role> roles = roleAppService.listAllRoles();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", roles);
        return result;
    }
    
    /**
     * 根据类型查询角色
     */
    @GetMapping("/type/{roleType}")
    public Map<String, Object> listRolesByType(@PathVariable String roleType) {
        log.info("接收根据类型查询角色请求: roleType={}", roleType);
        
        List<Role> roles = roleAppService.listRolesByType(roleType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", roles);
        return result;
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/{roleId}/permissions")
    public Map<String, Object> assignPermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody AssignPermissionsCmd cmd) {
        log.info("接收分配权限请求: roleId={}, cmd={}", roleId, cmd);
        
        cmd.setRoleId(roleId);
        roleAppService.assignPermissions(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "权限分配成功");
        return result;
    }
    
    /**
     * 查询角色的权限列表
     */
    @GetMapping("/{roleId}/permissions")
    public Map<String, Object> listPermissionsByRoleId(@PathVariable Long roleId) {
        log.info("接收查询角色权限请求: roleId={}", roleId);
        
        List<Long> permissionIds = roleAppService.listPermissionIdsByRoleId(roleId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", permissionIds);
        return result;
    }
}

