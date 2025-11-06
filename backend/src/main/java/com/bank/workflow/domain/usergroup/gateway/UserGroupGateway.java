package com.bank.workflow.domain.usergroup.gateway;

import com.bank.workflow.domain.usergroup.entity.UserGroup;

import java.util.List;

/**
 * 用户组网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface UserGroupGateway {
    
    Long createUserGroup(UserGroup userGroup);
    
    void updateUserGroup(UserGroup userGroup);
    
    void deleteUserGroup(Long groupId);
    
    UserGroup getUserGroupById(Long groupId);
    
    UserGroup getUserGroupByCode(String groupCode);
    
    List<UserGroup> listAllUserGroups();
    
    /**
     * 添加用户到用户组
     */
    void addMembersToGroup(Long groupId, List<Long> userIds);
    
    /**
     * 从用户组移除用户
     */
    void removeMembersFromGroup(Long groupId, List<Long> userIds);
    
    /**
     * 查询用户组的成员ID列表
     */
    List<Long> listMemberIdsByGroupId(Long groupId);
    
    /**
     * 查询用户所属的用户组ID列表
     */
    List<Long> listGroupIdsByUserId(Long userId);
}

