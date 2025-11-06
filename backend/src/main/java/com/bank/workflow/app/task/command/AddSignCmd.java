package com.bank.workflow.app.task.command;

import lombok.Data;

import java.util.List;

/**
 * 加签任务命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class AddSignCmd {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 要添加的用户ID列表
     */
    private List<String> addUserIds;
}

