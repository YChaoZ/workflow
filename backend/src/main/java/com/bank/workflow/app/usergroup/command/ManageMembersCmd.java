package com.bank.workflow.app.usergroup.command;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class ManageMembersCmd {
    
    @NotNull(message = "用户组ID不能为空")
    private Long groupId;
    
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;
}

