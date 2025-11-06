package com.bank.workflow.domain.process.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 历史流程实例
 *
 * @author workflow
 * @since 2025-11-05
 */
@Data
public class HistoricProcessInstance {
    
    /** 流程实例ID */
    private String instanceId;
    
    /** 流程定义ID */
    private String processDefinitionId;
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 流程名称 */
    private String processName;
    
    /** 流程版本 */
    private Integer processVersion;
    
    /** 发起人 */
    private String startUser;
    
    /** 开始时间 */
    private Date startTime;
    
    /** 结束时间 */
    private Date endTime;
    
    /** 持续时间（毫秒） */
    private Long duration;
    
    /** 删除原因 */
    private String deleteReason;
    
    /** 流程变量 */
    private Map<String, Object> variables;
}

