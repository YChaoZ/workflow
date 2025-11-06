package com.bank.workflow.app.comment.command;

import lombok.Data;

/**
 * 添加任务意见命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class AddCommentCmd {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 意见类型（approve:同意, reject:拒绝, comment:评论）
     */
    private String commentType;
    
    /**
     * 意见内容
     */
    private String content;
    
    /**
     * 评论人用户ID
     */
    private String userId;
    
    /**
     * 评论人姓名
     */
    private String userName;
}

