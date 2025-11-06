package com.bank.workflow.domain.monitor.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程监控实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessMonitor {
    
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
     * 业务Key
     */
    private String businessKey;
    
    /**
     * 流程名称
     */
    private String processName;
    
    /**
     * 发起人
     */
    private String startUser;
    
    /**
     * 发起人名称
     */
    private String startUserName;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 当前节点
     */
    private String currentNodeName;
    
    /**
     * 当前处理人
     */
    private String currentAssignee;
    
    /**
     * 当前处理人名称
     */
    private String currentAssigneeName;
    
    /**
     * 运行时长（毫秒）
     */
    private Long duration;
    
    /**
     * 是否超时
     */
    private Boolean timeout;
    
    /**
     * 流程状态：running-运行中，suspended-挂起
     */
    private String status;
    
    /**
     * 是否异常
     */
    private Boolean exception;
    
    /**
     * 异常原因
     */
    private String exceptionReason;
}

