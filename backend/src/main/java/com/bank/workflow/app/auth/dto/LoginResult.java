package com.bank.workflow.app.auth.dto;

import lombok.Data;

/**
 * 登录结果
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class LoginResult {
    
    /** 访问令牌 */
    private String accessToken;
    
    /** 令牌类型 */
    private String tokenType = "Bearer";
    
    /** 过期时间（秒） */
    private Long expiresIn;
    
    /** 用户信息 */
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        /** 用户ID */
        private Long userId;
        
        /** 用户名 */
        private String username;
        
        /** 真实姓名 */
        private String realName;
        
        /** 部门ID */
        private Long deptId;
        
        /** 职位 */
        private String position;
    }
}

