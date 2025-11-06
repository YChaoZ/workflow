package com.bank.workflow.domain.user.entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class User {
    
    /** 用户ID */
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /**
     * 获取用户ID（兼容性方法）
     */
    public Long getUserId() {
        return this.id;
    }
    
    /**
     * 设置用户ID（兼容性方法）
     */
    public void setUserId(Long userId) {
        this.id = userId;
    }
    
    /** 真实姓名 */
    private String realName;
    
    /** 密码（加密后） */
    private String password;
    
    /** 部门ID */
    private Long deptId;
    
    /** 职位 */
    private String position;
    
    /** 邮箱 */
    private String email;
    
    /** 手机号 */
    private String mobile;
    
    /** 状态：0-禁用，1-启用 */
    private Integer status;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
    
    /** 角色ID列表 */
    private List<Long> roleIds = new ArrayList<>();
    
    /**
     * 判断用户是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    /**
     * 启用用户
     */
    public void enable() {
        this.status = 1;
    }
    
    /**
     * 禁用用户
     */
    public void disable() {
        this.status = 0;
    }
}

