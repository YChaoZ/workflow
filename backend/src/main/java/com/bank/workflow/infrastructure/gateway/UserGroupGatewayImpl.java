package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.usergroup.entity.UserGroup;
import com.bank.workflow.domain.usergroup.gateway.UserGroupGateway;
import com.bank.workflow.infrastructure.persistence.mapper.UserGroupMapper;
import com.bank.workflow.infrastructure.persistence.mapper.UserGroupMemberMapper;
import com.bank.workflow.infrastructure.persistence.po.UserGroupDO;
import com.bank.workflow.infrastructure.persistence.po.UserGroupMemberDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserGroupGatewayImpl implements UserGroupGateway {
    
    private final UserGroupMapper userGroupMapper;
    private final UserGroupMemberMapper memberMapper;
    
    @Override
    public Long createUserGroup(UserGroup userGroup) {
        log.info("创建用户组: userGroup={}", userGroup);
        
        UserGroupDO userGroupDO = new UserGroupDO();
        BeanUtils.copyProperties(userGroup, userGroupDO);
        userGroupDO.setCreateTime(new Date());
        userGroupDO.setUpdateTime(new Date());
        
        userGroupMapper.insert(userGroupDO);
        
        log.info("用户组创建成功: groupId={}", userGroupDO.getId());
        return userGroupDO.getId();
    }
    
    @Override
    public void updateUserGroup(UserGroup userGroup) {
        log.info("更新用户组: userGroup={}", userGroup);
        
        UserGroupDO userGroupDO = new UserGroupDO();
        BeanUtils.copyProperties(userGroup, userGroupDO);
        userGroupDO.setUpdateTime(new Date());
        
        userGroupMapper.updateById(userGroupDO);
        
        log.info("用户组更新成功: groupId={}", userGroup.getId());
    }
    
    @Override
    public void deleteUserGroup(Long groupId) {
        log.info("删除用户组: groupId={}", groupId);
        
        userGroupMapper.deleteById(groupId);
        
        // 删除成员关联
        LambdaQueryWrapper<UserGroupMemberDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroupMemberDO::getGroupId, groupId);
        memberMapper.delete(wrapper);
        
        log.info("用户组删除成功: groupId={}", groupId);
    }
    
    @Override
    public UserGroup getUserGroupById(Long groupId) {
        log.info("查询用户组详情: groupId={}", groupId);
        
        UserGroupDO userGroupDO = userGroupMapper.selectById(groupId);
        if (userGroupDO == null) {
            log.warn("用户组不存在: groupId={}", groupId);
            return null;
        }
        
        UserGroup userGroup = convertToEntity(userGroupDO);
        userGroup.setMemberIds(listMemberIdsByGroupId(groupId));
        
        return userGroup;
    }
    
    @Override
    public UserGroup getUserGroupByCode(String groupCode) {
        log.info("根据编码查询用户组: groupCode={}", groupCode);
        
        LambdaQueryWrapper<UserGroupDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroupDO::getGroupCode, groupCode);
        
        UserGroupDO userGroupDO = userGroupMapper.selectOne(wrapper);
        if (userGroupDO == null) {
            log.warn("用户组不存在: groupCode={}", groupCode);
            return null;
        }
        
        return convertToEntity(userGroupDO);
    }
    
    @Override
    public List<UserGroup> listAllUserGroups() {
        log.info("查询所有用户组");
        
        List<UserGroupDO> list = userGroupMapper.selectList(null);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMembersToGroup(Long groupId, List<Long> userIds) {
        log.info("添加用户到用户组: groupId={}, userIds={}", groupId, userIds);
        
        Date now = new Date();
        for (Long userId : userIds) {
            // 检查是否已存在
            LambdaQueryWrapper<UserGroupMemberDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserGroupMemberDO::getGroupId, groupId);
            wrapper.eq(UserGroupMemberDO::getUserId, userId);
            
            Long count = memberMapper.selectCount(wrapper);
            if (count == 0) {
                UserGroupMemberDO memberDO = new UserGroupMemberDO();
                memberDO.setGroupId(groupId);
                memberDO.setUserId(userId);
                memberDO.setCreateTime(now);
                memberMapper.insert(memberDO);
            }
        }
        
        log.info("添加成功: groupId={}, 添加{}个成员", groupId, userIds.size());
    }
    
    @Override
    public void removeMembersFromGroup(Long groupId, List<Long> userIds) {
        log.info("从用户组移除用户: groupId={}, userIds={}", groupId, userIds);
        
        LambdaQueryWrapper<UserGroupMemberDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroupMemberDO::getGroupId, groupId);
        wrapper.in(UserGroupMemberDO::getUserId, userIds);
        
        memberMapper.delete(wrapper);
        
        log.info("移除成功: groupId={}, 移除{}个成员", groupId, userIds.size());
    }
    
    @Override
    public List<Long> listMemberIdsByGroupId(Long groupId) {
        log.info("查询用户组成员: groupId={}", groupId);
        
        LambdaQueryWrapper<UserGroupMemberDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroupMemberDO::getGroupId, groupId);
        
        List<UserGroupMemberDO> list = memberMapper.selectList(wrapper);
        return list.stream().map(UserGroupMemberDO::getUserId).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> listGroupIdsByUserId(Long userId) {
        log.info("查询用户所属用户组: userId={}", userId);
        
        LambdaQueryWrapper<UserGroupMemberDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroupMemberDO::getUserId, userId);
        
        List<UserGroupMemberDO> list = memberMapper.selectList(wrapper);
        return list.stream().map(UserGroupMemberDO::getGroupId).collect(Collectors.toList());
    }
    
    private UserGroup convertToEntity(UserGroupDO userGroupDO) {
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupDO, userGroup);
        return userGroup;
    }
}

