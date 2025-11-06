package com.bank.workflow.adapter.web;

import com.bank.workflow.app.usergroup.UserGroupAppService;
import com.bank.workflow.app.usergroup.command.CreateUserGroupCmd;
import com.bank.workflow.app.usergroup.command.ManageMembersCmd;
import com.bank.workflow.app.usergroup.command.UpdateUserGroupCmd;
import com.bank.workflow.domain.usergroup.entity.UserGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user-groups")
@RequiredArgsConstructor
public class UserGroupController {
    
    private final UserGroupAppService userGroupAppService;
    
    @PostMapping
    public Map<String, Object> createUserGroup(@Valid @RequestBody CreateUserGroupCmd cmd) {
        Long groupId = userGroupAppService.createUserGroup(cmd);
        return Map.of("code", 200, "message", "创建成功", "data", Map.of("groupId", groupId));
    }
    
    @PutMapping("/{groupId}")
    public Map<String, Object> updateUserGroup(@PathVariable Long groupId, @Valid @RequestBody UpdateUserGroupCmd cmd) {
        cmd.setId(groupId);
        userGroupAppService.updateUserGroup(cmd);
        return Map.of("code", 200, "message", "更新成功");
    }
    
    @DeleteMapping("/{groupId}")
    public Map<String, Object> deleteUserGroup(@PathVariable Long groupId) {
        userGroupAppService.deleteUserGroup(groupId);
        return Map.of("code", 200, "message", "删除成功");
    }
    
    @GetMapping("/{groupId}")
    public Map<String, Object> getUserGroupById(@PathVariable Long groupId) {
        UserGroup userGroup = userGroupAppService.getUserGroupById(groupId);
        return Map.of("code", 200, "message", "查询成功", "data", userGroup);
    }
    
    @GetMapping("/list")
    public Map<String, Object> listAllUserGroups() {
        List<UserGroup> userGroups = userGroupAppService.listAllUserGroups();
        return Map.of("code", 200, "message", "查询成功", "data", userGroups);
    }
    
    @PostMapping("/{groupId}/members")
    public Map<String, Object> addMembers(@PathVariable Long groupId, @Valid @RequestBody ManageMembersCmd cmd) {
        cmd.setGroupId(groupId);
        userGroupAppService.addMembers(cmd);
        return Map.of("code", 200, "message", "添加成员成功");
    }
    
    @DeleteMapping("/{groupId}/members")
    public Map<String, Object> removeMembers(@PathVariable Long groupId, @Valid @RequestBody ManageMembersCmd cmd) {
        cmd.setGroupId(groupId);
        userGroupAppService.removeMembers(cmd);
        return Map.of("code", 200, "message", "移除成员成功");
    }
    
    @GetMapping("/{groupId}/members")
    public Map<String, Object> listMembers(@PathVariable Long groupId) {
        List<Long> memberIds = userGroupAppService.listMemberIdsByGroupId(groupId);
        return Map.of("code", 200, "message", "查询成功", "data", memberIds);
    }
}

