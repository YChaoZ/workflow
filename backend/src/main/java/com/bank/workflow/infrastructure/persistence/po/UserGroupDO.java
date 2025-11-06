package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户组持久化对象
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_user_group")
public class UserGroupDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String groupCode;
    private String groupName;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    
    @TableLogic
    private Integer deleted;
}

