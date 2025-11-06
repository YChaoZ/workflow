package com.bank.workflow.infrastructure.redis;

import com.bank.workflow.domain.permission.entity.Permission;
import com.bank.workflow.domain.role.gateway.RoleGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final CacheService cacheService;
    private final RoleGateway roleGateway;

    private static final String PERMISSION_CACHE_PREFIX = "permission:";
    private static final String USER_PERMISSION_CACHE_PREFIX = "user:permission:";
    private static final String ROLE_PERMISSION_CACHE_PREFIX = "role:permission:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    /**
     * 获取用户权限（带缓存）
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    public List<String> getUserPermissions(Long userId) {
        String cacheKey = USER_PERMISSION_CACHE_PREFIX + userId;
        
        // 尝试从缓存获取
        List<String> cachedPermissions = cacheService.get(cacheKey);
        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            log.debug("从缓存获取用户权限: userId={}, count={}", userId, cachedPermissions.size());
            return cachedPermissions;
        }
        
        // 从数据库加载（这里需要实现）
        // TODO: 实现从数据库加载用户权限的逻辑
        List<String> permissions = loadUserPermissionsFromDB(userId);
        
        // 存入缓存
        if (permissions != null && !permissions.isEmpty()) {
            cacheService.set(cacheKey, permissions, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            log.debug("用户权限已缓存: userId={}, count={}", userId, permissions.size());
        }
        
        return permissions;
    }

    /**
     * 获取角色权限（带缓存）
     *
     * @param roleId 角色ID
     * @return 权限编码列表
     */
    public List<String> getRolePermissions(Long roleId) {
        String cacheKey = ROLE_PERMISSION_CACHE_PREFIX + roleId;
        
        // 尝试从缓存获取
        List<String> cachedPermissions = cacheService.get(cacheKey);
        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            log.debug("从缓存获取角色权限: roleId={}, count={}", roleId, cachedPermissions.size());
            return cachedPermissions;
        }
        
        // 从数据库加载
        List<String> permissions = loadRolePermissionsFromDB(roleId);
        
        // 存入缓存
        if (permissions != null && !permissions.isEmpty()) {
            cacheService.set(cacheKey, permissions, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            log.debug("角色权限已缓存: roleId={}, count={}", roleId, permissions.size());
        }
        
        return permissions;
    }

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户ID
     */
    public void clearUserPermissionCache(Long userId) {
        String cacheKey = USER_PERMISSION_CACHE_PREFIX + userId;
        cacheService.delete(cacheKey);
        log.info("清除用户权限缓存: userId={}", userId);
    }

    /**
     * 清除角色权限缓存
     *
     * @param roleId 角色ID
     */
    public void clearRolePermissionCache(Long roleId) {
        String cacheKey = ROLE_PERMISSION_CACHE_PREFIX + roleId;
        cacheService.delete(cacheKey);
        log.info("清除角色权限缓存: roleId={}", roleId);
    }

    /**
     * 清除所有权限缓存
     */
    public void clearAllPermissionCache() {
        cacheService.deleteByPattern(PERMISSION_CACHE_PREFIX + "*");
        cacheService.deleteByPattern(USER_PERMISSION_CACHE_PREFIX + "*");
        cacheService.deleteByPattern(ROLE_PERMISSION_CACHE_PREFIX + "*");
        log.info("清除所有权限缓存");
    }

    /**
     * 从数据库加载用户权限
     */
    private List<String> loadUserPermissionsFromDB(Long userId) {
        // TODO: 实现从数据库加载用户所有权限的逻辑
        // 1. 查询用户的所有角色
        // 2. 查询每个角色的权限
        // 3. 合并去重
        log.debug("从数据库加载用户权限: userId={}", userId);
        return List.of(); // 暂时返回空列表
    }

    /**
     * 从数据库加载角色权限
     */
    private List<String> loadRolePermissionsFromDB(Long roleId) {
        try {
            // 查询角色的权限ID列表
            List<Long> permissionIds = roleGateway.listPermissionIdsByRoleId(roleId);
            
            // 转换为权限编码（这里简化处理，实际应该查询完整的权限信息）
            List<String> permissionCodes = permissionIds.stream()
                    .map(id -> "PERM_" + id) // 简化处理
                    .collect(Collectors.toList());
            
            log.debug("从数据库加载角色权限: roleId={}, count={}", roleId, permissionCodes.size());
            return permissionCodes;
        } catch (Exception e) {
            log.error("加载角色权限失败: roleId={}", roleId, e);
            return List.of();
        }
    }
}

