package com.bank.workflow.domain.permission.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 权限领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class Permission {
    
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 权限编码
     */
    private String permissionCode;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限类型：MENU-菜单，BUTTON-按钮，API-接口
     */
    private String permissionType;
    
    /**
     * 父权限ID（0表示根权限）
     */
    private Long parentId;
    
    /**
     * 资源路径（菜单路由或API路径）
     */
    private String resourcePath;
    
    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    private String resourceMethod;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 子权限列表（用于构建树形结构）
     */
    private List<Permission> children = new ArrayList<>();
    
    /**
     * 判断是否为根权限
     */
    public boolean isRoot() {
        return parentId == null || parentId == 0;
    }
    
    /**
     * 判断是否为菜单
     */
    public boolean isMenu() {
        return "MENU".equals(permissionType);
    }
    
    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}

