package com.bank.workflow.app.task.command;

import lombok.Data;

/**
 * 退回任务命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class RejectTaskCmd {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 目标节点ID
     */
    private String targetNodeId;
    
    /**
     * 退回原因
     */
    private String reason;
}

