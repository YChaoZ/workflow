package com.bank.workflow.app.auth.command;

import lombok.Data;

/**
 * 登录命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class LoginCmd {
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
}

