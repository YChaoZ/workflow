package com.bank.workflow.app.usergroup.command;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateUserGroupCmd {
    
    @NotNull(message = "用户组ID不能为空")
    private Long id;
    
    @NotBlank(message = "用户组编码不能为空")
    private String groupCode;
    
    @NotBlank(message = "用户组名称不能为空")
    private String groupName;
    
    private String description;
    private Integer status;
    private List<Long> memberIds;
}

