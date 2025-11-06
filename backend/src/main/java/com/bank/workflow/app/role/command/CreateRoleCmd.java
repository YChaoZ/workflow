package com.bank.workflow.app.role.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建角色命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateRoleCmd {
    
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
     * 权限ID列表
     */
    private List<Long> permissionIds;
}

