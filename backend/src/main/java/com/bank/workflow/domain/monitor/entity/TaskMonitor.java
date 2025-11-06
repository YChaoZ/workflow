package com.bank.workflow.domain.monitor.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务监控实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class TaskMonitor {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 流程定义Key
     */
    private String processDefinitionKey;
    
    /**
     * 流程定义名称
     */
    private String processDefinitionName;
    
    /**
     * 处理人
     */
    private String assignee;
    
    /**
     * 处理人名称
     */
    private String assigneeName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 到期时间
     */
    private LocalDateTime dueDate;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 等待时长（毫秒）
     */
    private Long waitingDuration;
    
    /**
     * 是否超时
     */
    private Boolean timeout;
    
    /**
     * 任务状态
     */
    private String status;
}

