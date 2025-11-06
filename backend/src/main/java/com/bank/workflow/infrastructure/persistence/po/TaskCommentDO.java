package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务意见持久化对象
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("wf_task_comment")
public class TaskCommentDO {
    
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 意见类型（APPROVE-同意, REJECT-拒绝, TRANSFER-转办）
     */
    private String commentType;
    
    /**
     * 意见内容
     */
    private String commentText;
    
    /**
     * 创建时间
     */
    private Date createdTime;
}
