package com.bank.workflow.app.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建用户命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateUserCmd {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

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
    private Boolean status = true;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
}

