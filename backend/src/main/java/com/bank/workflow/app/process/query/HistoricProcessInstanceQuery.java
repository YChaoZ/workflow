package com.bank.workflow.app.process.query;

import lombok.Data;

/**
 * 历史流程实例查询参数
 *
 * @author workflow
 * @since 2025-11-05
 */
@Data
public class HistoricProcessInstanceQuery {
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 发起人 */
    private String startUser;
    
    /** 是否已完成 */
    private Boolean finished;
    
    /** 开始时间（起） */
    private String startTimeBegin;
    
    /** 开始时间（止） */
    private String startTimeEnd;
    
    /** 结束时间（起） */
    private String endTimeBegin;
    
    /** 结束时间（止） */
    private String endTimeEnd;
    
    /** 页码 */
    private Integer pageNum = 1;
    
    /** 每页大小 */
    private Integer pageSize = 10;
    
    /** 排序字段 */
    private String orderBy = "startTime";
    
    /** 排序方向 */
    private String orderDirection = "desc";
}

