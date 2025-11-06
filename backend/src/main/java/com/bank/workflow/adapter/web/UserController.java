package com.bank.workflow.adapter.web;

import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.user.UserAppService;
import com.bank.workflow.app.user.command.AssignRolesCmd;
import com.bank.workflow.app.user.command.CreateUserCmd;
import com.bank.workflow.app.user.command.UpdateUserCmd;
import com.bank.workflow.app.user.query.UserPageQuery;
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
    
    @PostMapping
    public Map<String, Object> createUser(@Valid @RequestBody CreateUserCmd cmd) {
        Long userId = userAppService.createUser(cmd);
        return Map.of("code", 200, "message", "创建成功", "data", userId);
    }
    
    @PutMapping("/{userId}")
    public Map<String, Object> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserCmd cmd) {
        cmd.setId(userId);
        userAppService.updateUser(cmd);
        return Map.of("code", 200, "message", "更新成功");
    }
    
    @DeleteMapping("/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        userAppService.deleteUser(userId);
        return Map.of("code", 200, "message", "删除成功");
    }
    
    @GetMapping("/page")
    public Map<String, Object> pageUsers(UserPageQuery query) {
        PageResult<User> result = userAppService.pageUsers(query);
        return Map.of("code", 200, "message", "查询成功", "data", result);
    }
}

