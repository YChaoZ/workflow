package com.bank.workflow.domain.statistics.entity;

import lombok.Data;

/**
 * 流程统计实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessStatistics {
    
    /**
     * 流程定义Key
     */
    private String processDefinitionKey;
    
    /**
     * 流程定义名称
     */
    private String processDefinitionName;
    
    /**
     * 总启动数量
     */
    private Long totalStarted;
    
    /**
     * 总完成数量
     */
    private Long totalCompleted;
    
    /**
     * 运行中数量
     */
    private Long totalRunning;
    
    /**
     * 平均耗时（毫秒）
     */
    private Long avgDuration;
    
    /**
     * 最短耗时（毫秒）
     */
    private Long minDuration;
    
    /**
     * 最长耗时（毫秒）
     */
    private Long maxDuration;
}

