package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户角色关联持久化对象
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_user_role")
public class UserRoleDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long roleId;
    private Date createTime;
}

