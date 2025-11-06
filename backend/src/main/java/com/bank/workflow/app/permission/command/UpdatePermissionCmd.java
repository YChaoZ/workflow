package com.bank.workflow.app.permission.command;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新权限命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UpdatePermissionCmd {
    
    @NotNull(message = "权限ID不能为空")
    private Long id;
    
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;
    
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;
    
    @NotBlank(message = "权限类型不能为空")
    private String permissionType;
    
    private Long parentId;
    private String resourcePath;
    private String resourceMethod;
    private String icon;
    private Integer sortOrder;
    private String description;
    private Integer status;
}

