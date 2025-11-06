package com.bank.workflow.app.task.query;

import lombok.Data;

/**
 * 任务查询条件
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class TaskQuery {
    
    /** 任务办理人 */
    private String assignee;
    
    /** 候选用户 */
    private String candidateUser;
    
    /** 候选组 */
    private String candidateGroup;
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 流程实例ID */
    private String processInstanceId;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 任务名称（模糊查询） */
    private String taskName;
    
    /** 任务状态：todo-待办, done-已办 */
    private String taskStatus = "todo";
    
    /** 页码，从1开始 */
    private Integer pageNum = 1;
    
    /** 每页大小 */
    private Integer pageSize = 10;
    
    /** 排序字段：createTime, priority */
    private String orderBy = "createTime";
    
    /** 排序方向：asc, desc */
    private String orderDirection = "desc";
}

