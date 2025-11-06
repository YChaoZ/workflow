package com.bank.workflow.app.role.command;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分配权限命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class AssignPermissionsCmd {
    
    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    
    /**
     * 权限ID列表
     */
    @NotNull(message = "权限列表不能为空")
    private List<Long> permissionIds;
}

