package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.permission.entity.Permission;
import com.bank.workflow.domain.permission.gateway.PermissionGateway;
import com.bank.workflow.infrastructure.persistence.mapper.PermissionMapper;
import com.bank.workflow.infrastructure.persistence.po.PermissionDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionGatewayImpl implements PermissionGateway {
    
    private final PermissionMapper permissionMapper;
    
    @Override
    public Long createPermission(Permission permission) {
        log.info("创建权限: permission={}", permission);
        
        PermissionDO permissionDO = new PermissionDO();
        BeanUtils.copyProperties(permission, permissionDO);
        permissionDO.setCreateTime(new Date());
        permissionDO.setUpdateTime(new Date());
        
        permissionMapper.insert(permissionDO);
        
        log.info("权限创建成功: permissionId={}", permissionDO.getId());
        return permissionDO.getId();
    }
    
    @Override
    public void updatePermission(Permission permission) {
        log.info("更新权限: permission={}", permission);
        
        PermissionDO permissionDO = new PermissionDO();
        BeanUtils.copyProperties(permission, permissionDO);
        permissionDO.setUpdateTime(new Date());
        
        permissionMapper.updateById(permissionDO);
        
        log.info("权限更新成功: permissionId={}", permission.getId());
    }
    
    @Override
    public void deletePermission(Long permissionId) {
        log.info("删除权限: permissionId={}", permissionId);
        permissionMapper.deleteById(permissionId);
        log.info("权限删除成功: permissionId={}", permissionId);
    }
    
    @Override
    public Permission getPermissionById(Long permissionId) {
        log.info("查询权限详情: permissionId={}", permissionId);
        PermissionDO permissionDO = permissionMapper.selectById(permissionId);
        if (permissionDO == null) {
            log.warn("权限不存在: permissionId={}", permissionId);
            return null;
        }
        return convertToEntity(permissionDO);
    }
    
    @Override
    public Permission getPermissionByCode(String permissionCode) {
        log.info("根据编码查询权限: permissionCode={}", permissionCode);
        
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionDO::getPermissionCode, permissionCode);
        
        PermissionDO permissionDO = permissionMapper.selectOne(wrapper);
        if (permissionDO == null) {
            log.warn("权限不存在: permissionCode={}", permissionCode);
            return null;
        }
        return convertToEntity(permissionDO);
    }
    
    @Override
    public List<Permission> listAllPermissions() {
        log.info("查询所有权限");
        
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PermissionDO::getSortOrder, PermissionDO::getId);
        
        List<PermissionDO> list = permissionMapper.selectList(wrapper);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> listPermissionsByType(String permissionType) {
        log.info("根据类型查询权限: permissionType={}", permissionType);
        
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionDO::getPermissionType, permissionType);
        wrapper.orderByAsc(PermissionDO::getSortOrder, PermissionDO::getId);
        
        List<PermissionDO> list = permissionMapper.selectList(wrapper);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> listChildrenPermissions(Long parentId) {
        log.info("查询子权限: parentId={}", parentId);
        
        LambdaQueryWrapper<PermissionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionDO::getParentId, parentId);
        wrapper.orderByAsc(PermissionDO::getSortOrder, PermissionDO::getId);
        
        List<PermissionDO> list = permissionMapper.selectList(wrapper);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> getPermissionTree() {
        log.info("查询权限树");
        
        List<Permission> allPermissions = listAllPermissions();
        
        Map<Long, Permission> permMap = allPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, perm -> perm));
        
        List<Permission> roots = new ArrayList<>();
        for (Permission perm : allPermissions) {
            if (perm.getParentId() == null || perm.getParentId() == 0) {
                roots.add(perm);
            } else {
                Permission parent = permMap.get(perm.getParentId());
                if (parent != null) {
                    parent.getChildren().add(perm);
                } else {
                    roots.add(perm);
                }
            }
        }
        
        log.info("权限树构建完成，根权限数量: {}", roots.size());
        return roots;
    }
    
    @Override
    public List<Permission> listPermissionsByIds(List<Long> permissionIds) {
        log.info("批量查询权限: permissionIds={}", permissionIds);
        
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PermissionDO> list = permissionMapper.selectBatchIds(permissionIds);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }
    
    private Permission convertToEntity(PermissionDO permissionDO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDO, permission);
        return permission;
    }
}

