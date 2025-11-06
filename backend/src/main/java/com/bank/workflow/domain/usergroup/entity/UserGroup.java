package com.bank.workflow.domain.usergroup.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户组领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UserGroup {
    
    /**
     * 用户组ID
     */
    private Long id;
    
    /**
     * 用户组编码
     */
    private String groupCode;
    
    /**
     * 用户组名称
     */
    private String groupName;
    
    /**
     * 用户组描述
     */
    private String description;
    
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 成员用户ID列表
     */
    private List<Long> memberIds = new ArrayList<>();
    
    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}

