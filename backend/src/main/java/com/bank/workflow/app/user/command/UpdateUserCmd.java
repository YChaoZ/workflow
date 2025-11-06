package com.bank.workflow.app.user.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 更新用户命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UpdateUserCmd {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 状态：0-停用，1-启用
     */
    private Boolean status;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
}

