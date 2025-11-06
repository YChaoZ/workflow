package com.bank.workflow.app.role.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新角色命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UpdateRoleCmd {
    
    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;
    
    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    
    /**
     * 角色类型
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
     * 权限ID列表（可选，如果提供则更新权限）
     */
    private List<Long> permissionIds;
}

