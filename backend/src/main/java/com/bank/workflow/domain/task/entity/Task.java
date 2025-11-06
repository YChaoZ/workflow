package com.bank.workflow.domain.task.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 任务领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class Task {
    
    /** 任务ID */
    private String taskId;
    
    /** 任务名称 */
    private String taskName;
    
    /** 任务定义KEY */
    private String taskKey;
    
    /** 流程实例ID */
    private String processInstanceId;
    
    /** 流程定义ID */
    private String processDefinitionId;
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 任务办理人 */
    private String assignee;
    
    /** 任务所有者 */
    private String owner;
    
    /** 候选用户 */
    private String candidateUsers;
    
    /** 候选组 */
    private String candidateGroups;
    
    /** 任务创建时间 */
    private Date createTime;
    
    /** 任务完成时间 */
    private Date endTime;
    
    /** 任务状态 */
    private String status;
    
    /** 任务优先级 */
    private Integer priority;
    
    /** 任务描述 */
    private String description;
    
    /** 表单KEY */
    private String formKey;
    
    /** 任务变量 */
    private Map<String, Object> variables;
}

