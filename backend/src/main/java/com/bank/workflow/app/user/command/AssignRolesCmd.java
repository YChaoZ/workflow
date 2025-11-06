package com.bank.workflow.app.user.command;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 为用户分配角色命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class AssignRolesCmd {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "角色列表不能为空")
    private List<Long> roleIds;
}

