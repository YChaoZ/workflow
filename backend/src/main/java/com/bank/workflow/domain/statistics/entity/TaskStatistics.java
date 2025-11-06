package com.bank.workflow.domain.statistics.entity;

import lombok.Data;

/**
 * 任务统计实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class TaskStatistics {
    
    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 流程定义Key
     */
    private String processDefinitionKey;
    
    /**
     * 总任务数量
     */
    private Long totalTasks;
    
    /**
     * 已完成任务数量
     */
    private Long completedTasks;
    
    /**
     * 待处理任务数量
     */
    private Long pendingTasks;
    
    /**
     * 平均处理时长（毫秒）
     */
    private Long avgDuration;
    
    /**
     * 最短处理时长（毫秒）
     */
    private Long minDuration;
    
    /**
     * 最长处理时长（毫秒）
     */
    private Long maxDuration;
}

