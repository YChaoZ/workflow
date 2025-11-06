package com.bank.workflow.app.task.command;

import lombok.Data;

import java.util.Map;

/**
 * 完成任务命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CompleteTaskCmd {
    
    /** 任务ID */
    private String taskId;
    
    /** 办理人 */
    private String assignee;
    
    /** 任务变量 */
    private Map<String, Object> variables;
    
    /** 办理意见 */
    private String comment;
}

