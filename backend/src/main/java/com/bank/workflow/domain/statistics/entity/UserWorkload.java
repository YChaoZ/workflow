package com.bank.workflow.domain.statistics.entity;

import lombok.Data;

/**
 * 用户工作量统计
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UserWorkload {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String username;
    
    /**
     * 总任务数量
     */
    private Long totalTasks;
    
    /**
     * 平均任务处理时长（毫秒）
     */
    private Long avgTaskDuration;
    
    /**
     * 总工作时长（毫秒）
     */
    private Long totalDuration;
}

