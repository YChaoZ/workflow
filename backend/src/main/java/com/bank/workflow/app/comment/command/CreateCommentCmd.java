package com.bank.workflow.app.comment.command;

import lombok.Data;

/**
 * 创建任务意见命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateCommentCmd {
    
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
}

