package com.bank.workflow.adapter.web;

import com.bank.workflow.app.permission.PermissionAppService;
import com.bank.workflow.app.permission.command.CreatePermissionCmd;
import com.bank.workflow.app.permission.command.UpdatePermissionCmd;
import com.bank.workflow.domain.permission.entity.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionAppService permissionAppService;
    
    @PostMapping
    public Map<String, Object> createPermission(@Valid @RequestBody CreatePermissionCmd cmd) {
        Long permissionId = permissionAppService.createPermission(cmd);
        return Map.of("code", 200, "message", "创建成功", "data", Map.of("permissionId", permissionId));
    }
    
    @PutMapping("/{permissionId}")
    public Map<String, Object> updatePermission(@PathVariable Long permissionId, @Valid @RequestBody UpdatePermissionCmd cmd) {
        cmd.setId(permissionId);
        permissionAppService.updatePermission(cmd);
        return Map.of("code", 200, "message", "更新成功");
    }
    
    @DeleteMapping("/{permissionId}")
    public Map<String, Object> deletePermission(@PathVariable Long permissionId) {
        permissionAppService.deletePermission(permissionId);
        return Map.of("code", 200, "message", "删除成功");
    }
    
    @GetMapping("/{permissionId}")
    public Map<String, Object> getPermissionById(@PathVariable Long permissionId) {
        Permission permission = permissionAppService.getPermissionById(permissionId);
        return Map.of("code", 200, "message", "查询成功", "data", permission);
    }
    
    @GetMapping("/list")
    public Map<String, Object> listAllPermissions() {
        List<Permission> permissions = permissionAppService.listAllPermissions();
        return Map.of("code", 200, "message", "查询成功", "data", permissions);
    }
    
    @GetMapping("/tree")
    public Map<String, Object> getPermissionTree() {
        List<Permission> tree = permissionAppService.getPermissionTree();
        return Map.of("code", 200, "message", "查询成功", "data", tree);
    }
    
    @GetMapping("/type/{permissionType}")
    public Map<String, Object> listPermissionsByType(@PathVariable String permissionType) {
        List<Permission> permissions = permissionAppService.listPermissionsByType(permissionType);
        return Map.of("code", 200, "message", "查询成功", "data", permissions);
    }
}

