package com.bank.workflow.domain.process.entity;

import lombok.Data;

import java.util.Date;

/**
 * 历史活动实例（流程执行轨迹）
 *
 * @author workflow
 * @since 2025-11-05
 */
@Data
public class HistoricActivityInstance {
    
    /** 活动ID */
    private String activityId;
    
    /** 活动名称 */
    private String activityName;
    
    /** 活动类型 */
    private String activityType;
    
    /** 流程实例ID */
    private String processInstanceId;
    
    /** 流程定义ID */
    private String processDefinitionId;
    
    /** 执行ID */
    private String executionId;
    
    /** 任务ID */
    private String taskId;
    
    /** 办理人 */
    private String assignee;
    
    /** 开始时间 */
    private Date startTime;
    
    /** 结束时间 */
    private Date endTime;
    
    /** 持续时间（毫秒） */
    private Long duration;
}

