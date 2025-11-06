package com.bank.workflow.app.process.query;

import lombok.Data;

/**
 * 流程实例查询条件
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessInstanceQuery {
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 发起人 */
    private String startUser;
    
    /** 流程状态：active-运行中, suspended-已挂起 */
    private String state;
    
    /** 流程标题（模糊查询） */
    private String title;
    
    /** 页码，从1开始 */
    private Integer pageNum = 1;
    
    /** 每页大小 */
    private Integer pageSize = 10;
    
    /** 排序字段：startTime, endTime */
    private String orderBy = "startTime";
    
    /** 排序方向：asc, desc */
    private String orderDirection = "desc";
}

