package com.bank.workflow.domain.role.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class Role {
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色类型：SYSTEM-系统角色，CUSTOM-自定义角色
     */
    private String roleType;
    
    /**
     * 角色描述
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
     * 权限ID列表
     */
    private List<Long> permissionIds = new ArrayList<>();
    
    /**
     * 判断是否为系统角色
     */
    public boolean isSystemRole() {
        return "SYSTEM".equals(roleType);
    }
    
    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}

