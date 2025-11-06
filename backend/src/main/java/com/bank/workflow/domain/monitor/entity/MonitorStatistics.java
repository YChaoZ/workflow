package com.bank.workflow.domain.monitor.entity;

import lombok.Data;

/**
 * 监控统计实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class MonitorStatistics {
    
    /**
     * 运行中流程数量
     */
    private Long runningProcessCount;
    
    /**
     * 挂起流程数量
     */
    private Long suspendedProcessCount;
    
    /**
     * 异常流程数量
     */
    private Long exceptionProcessCount;
    
    /**
     * 待办任务数量
     */
    private Long pendingTaskCount;
    
    /**
     * 超时任务数量
     */
    private Long timeoutTaskCount;
    
    /**
     * 今日启动流程数量
     */
    private Long todayStartedCount;
    
    /**
     * 今日完成流程数量
     */
    private Long todayCompletedCount;
    
    /**
     * 平均流程耗时（毫秒）
     */
    private Long avgProcessDuration;
    
    /**
     * 平均任务耗时（毫秒）
     */
    private Long avgTaskDuration;
}

