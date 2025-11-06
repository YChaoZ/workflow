package com.bank.workflow.adapter.web;

import com.bank.workflow.app.user.UserAppService;
import com.bank.workflow.app.user.command.AssignRolesCmd;
import com.bank.workflow.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserAppService userAppService;
    
    @GetMapping("/{userId}")
    public Map<String, Object> getUserById(@PathVariable Long userId) {
        User user = userAppService.getUserById(userId);
        return Map.of("code", 200, "message", "查询成功", "data", user);
    }
    
    @GetMapping("/list")
    public Map<String, Object> listAllUsers() {
        List<User> users = userAppService.listAllUsers();
        return Map.of("code", 200, "message", "查询成功", "data", users);
    }
    
    @PostMapping("/{userId}/roles")
    public Map<String, Object> assignRoles(@PathVariable Long userId, @Valid @RequestBody AssignRolesCmd cmd) {
        cmd.setUserId(userId);
        userAppService.assignRoles(cmd);
        return Map.of("code", 200, "message", "角色分配成功");
    }
    
    @GetMapping("/{userId}/roles")
    public Map<String, Object> listRolesByUserId(@PathVariable Long userId) {
        List<Long> roleIds = userAppService.listRoleIdsByUserId(userId);
        return Map.of("code", 200, "message", "查询成功", "data", roleIds);
    }
}

