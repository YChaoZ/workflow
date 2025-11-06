package com.bank.workflow.app.usergroup;

import com.bank.workflow.app.usergroup.command.CreateUserGroupCmd;
import com.bank.workflow.app.usergroup.command.ManageMembersCmd;
import com.bank.workflow.app.usergroup.command.UpdateUserGroupCmd;
import com.bank.workflow.domain.usergroup.entity.UserGroup;
import com.bank.workflow.domain.usergroup.gateway.UserGroupGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupAppService {
    
    private final UserGroupGateway userGroupGateway;
    
    @Transactional(rollbackFor = Exception.class)
    public Long createUserGroup(CreateUserGroupCmd cmd) {
        log.info("接收创建用户组请求: cmd={}", cmd);
        
        UserGroup existingGroup = userGroupGateway.getUserGroupByCode(cmd.getGroupCode());
        if (existingGroup != null) {
            throw new IllegalArgumentException("用户组编码已存在: " + cmd.getGroupCode());
        }
        
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(cmd, userGroup);
        
        if (userGroup.getStatus() == null) {
            userGroup.setStatus(1);
        }
        
        Long groupId = userGroupGateway.createUserGroup(userGroup);
        
        if (cmd.getMemberIds() != null && !cmd.getMemberIds().isEmpty()) {
            userGroupGateway.addMembersToGroup(groupId, cmd.getMemberIds());
        }
        
        log.info("用户组创建成功: groupId={}", groupId);
        return groupId;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updateUserGroup(UpdateUserGroupCmd cmd) {
        log.info("接收更新用户组请求: cmd={}", cmd);
        
        UserGroup existingGroup = userGroupGateway.getUserGroupById(cmd.getId());
        if (existingGroup == null) {
            throw new IllegalArgumentException("用户组不存在: " + cmd.getId());
        }
        
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(cmd, userGroup);
        
        userGroupGateway.updateUserGroup(userGroup);
        
        if (cmd.getMemberIds() != null) {
            // 清空现有成员
            List<Long> existingMemberIds = userGroupGateway.listMemberIdsByGroupId(cmd.getId());
            if (!existingMemberIds.isEmpty()) {
                userGroupGateway.removeMembersFromGroup(cmd.getId(), existingMemberIds);
            }
            
            // 添加新成员
            if (!cmd.getMemberIds().isEmpty()) {
                userGroupGateway.addMembersToGroup(cmd.getId(), cmd.getMemberIds());
            }
        }
        
        log.info("用户组更新成功: groupId={}", cmd.getId());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserGroup(Long groupId) {
        log.info("接收删除用户组请求: groupId={}", groupId);
        
        UserGroup userGroup = userGroupGateway.getUserGroupById(groupId);
        if (userGroup == null) {
            throw new IllegalArgumentException("用户组不存在: " + groupId);
        }
        
        userGroupGateway.deleteUserGroup(groupId);
        
        log.info("用户组删除成功: groupId={}", groupId);
    }
    
    public UserGroup getUserGroupById(Long groupId) {
        log.info("接收查询用户组详情请求: groupId={}", groupId);
        return userGroupGateway.getUserGroupById(groupId);
    }
    
    public List<UserGroup> listAllUserGroups() {
        log.info("接收查询所有用户组请求");
        return userGroupGateway.listAllUserGroups();
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void addMembers(ManageMembersCmd cmd) {
        log.info("接收添加成员请求: cmd={}", cmd);
        
        UserGroup userGroup = userGroupGateway.getUserGroupById(cmd.getGroupId());
        if (userGroup == null) {
            throw new IllegalArgumentException("用户组不存在: " + cmd.getGroupId());
        }
        
        userGroupGateway.addMembersToGroup(cmd.getGroupId(), cmd.getUserIds());
        
        log.info("添加成员成功: groupId={}, 添加{}个成员", cmd.getGroupId(), cmd.getUserIds().size());
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void removeMembers(ManageMembersCmd cmd) {
        log.info("接收移除成员请求: cmd={}", cmd);
        
        UserGroup userGroup = userGroupGateway.getUserGroupById(cmd.getGroupId());
        if (userGroup == null) {
            throw new IllegalArgumentException("用户组不存在: " + cmd.getGroupId());
        }
        
        userGroupGateway.removeMembersFromGroup(cmd.getGroupId(), cmd.getUserIds());
        
        log.info("移除成员成功: groupId={}, 移除{}个成员", cmd.getGroupId(), cmd.getUserIds().size());
    }
    
    public List<Long> listMemberIdsByGroupId(Long groupId) {
        log.info("接收查询用户组成员请求: groupId={}", groupId);
        return userGroupGateway.listMemberIdsByGroupId(groupId);
    }
}

