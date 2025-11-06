package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.role.entity.Role;
import com.bank.workflow.domain.role.gateway.RoleGateway;
import com.bank.workflow.infrastructure.persistence.mapper.RoleMapper;
import com.bank.workflow.infrastructure.persistence.mapper.RolePermissionMapper;
import com.bank.workflow.infrastructure.persistence.po.RoleDO;
import com.bank.workflow.infrastructure.persistence.po.RolePermissionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleGatewayImpl implements RoleGateway {
    
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    
    @Override
    public Long createRole(Role role) {
        log.info("创建角色: role={}", role);
        
        RoleDO roleDO = new RoleDO();
        BeanUtils.copyProperties(role, roleDO);
        roleDO.setCreateTime(new Date());
        roleDO.setUpdateTime(new Date());
        
        roleMapper.insert(roleDO);
        
        log.info("角色创建成功: roleId={}", roleDO.getId());
        return roleDO.getId();
    }
    
    @Override
    public void updateRole(Role role) {
        log.info("更新角色: role={}", role);
        
        RoleDO roleDO = new RoleDO();
        BeanUtils.copyProperties(role, roleDO);
        roleDO.setUpdateTime(new Date());
        
        roleMapper.updateById(roleDO);
        
        log.info("角色更新成功: roleId={}", role.getId());
    }
    
    @Override
    public void deleteRole(Long roleId) {
        log.info("删除角色: roleId={}", roleId);
        
        // 删除角色
        roleMapper.deleteById(roleId);
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        log.info("角色删除成功: roleId={}", roleId);
    }
    
    @Override
    public Role getRoleById(Long roleId) {
        log.info("查询角色详情: roleId={}", roleId);
        
        RoleDO roleDO = roleMapper.selectById(roleId);
        if (roleDO == null) {
            log.warn("角色不存在: roleId={}", roleId);
            return null;
        }
        
        Role role = convertToEntity(roleDO);
        
        // 查询角色权限
        role.setPermissionIds(listPermissionIdsByRoleId(roleId));
        
        return role;
    }
    
    @Override
    public Role getRoleByCode(String roleCode) {
        log.info("根据编码查询角色: roleCode={}", roleCode);
        
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getRoleCode, roleCode);
        
        RoleDO roleDO = roleMapper.selectOne(wrapper);
        if (roleDO == null) {
            log.warn("角色不存在: roleCode={}", roleCode);
            return null;
        }
        
        return convertToEntity(roleDO);
    }
    
    @Override
    public List<Role> listAllRoles() {
        log.info("查询所有角色");
        
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RoleDO::getId);
        
        List<RoleDO> roleDOList = roleMapper.selectList(wrapper);
        
        List<Role> result = new ArrayList<>();
        for (RoleDO roleDO : roleDOList) {
            result.add(convertToEntity(roleDO));
        }
        
        log.info("查询到{}个角色", result.size());
        return result;
    }
    
    @Override
    public List<Role> listRolesByType(String roleType) {
        log.info("根据类型查询角色: roleType={}", roleType);
        
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getRoleType, roleType);
        wrapper.orderByAsc(RoleDO::getId);
        
        List<RoleDO> roleDOList = roleMapper.selectList(wrapper);
        
        List<Role> result = new ArrayList<>();
        for (RoleDO roleDO : roleDOList) {
            result.add(convertToEntity(roleDO));
        }
        
        log.info("查询到{}个角色", result.size());
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("为角色分配权限: roleId={}, permissionIds={}", roleId, permissionIds);
        
        // 删除现有权限关联
        LambdaQueryWrapper<RolePermissionDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(RolePermissionDO::getRoleId, roleId);
        rolePermissionMapper.delete(deleteWrapper);
        
        // 插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Date now = new Date();
            for (Long permissionId : permissionIds) {
                RolePermissionDO rolePermissionDO = new RolePermissionDO();
                rolePermissionDO.setRoleId(roleId);
                rolePermissionDO.setPermissionId(permissionId);
                rolePermissionDO.setCreateTime(now);
                rolePermissionMapper.insert(rolePermissionDO);
            }
        }
        
        log.info("权限分配成功: roleId={}, 分配{}个权限", roleId, permissionIds == null ? 0 : permissionIds.size());
    }
    
    @Override
    public List<Long> listPermissionIdsByRoleId(Long roleId) {
        log.info("查询角色权限: roleId={}", roleId);
        
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermissionDO::getRoleId, roleId);
        
        List<RolePermissionDO> list = rolePermissionMapper.selectList(wrapper);
        
        List<Long> permissionIds = list.stream()
                .map(RolePermissionDO::getPermissionId)
                .collect(Collectors.toList());
        
        log.info("角色拥有{}个权限", permissionIds.size());
        return permissionIds;
    }
    
    @Override
    public List<Long> listPermissionIdsByRoleIds(List<Long> roleIds) {
        log.info("批量查询角色权限: roleIds={}", roleIds);
        
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<RolePermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RolePermissionDO::getRoleId, roleIds);
        
        List<RolePermissionDO> list = rolePermissionMapper.selectList(wrapper);
        
        // 去重
        List<Long> permissionIds = list.stream()
                .map(RolePermissionDO::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
        
        log.info("{}个角色共拥有{}个权限（去重后）", roleIds.size(), permissionIds.size());
        return permissionIds;
    }
    
    /**
     * 转换DO为领域实体
     */
    private Role convertToEntity(RoleDO roleDO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDO, role);
        return role;
    }
}

