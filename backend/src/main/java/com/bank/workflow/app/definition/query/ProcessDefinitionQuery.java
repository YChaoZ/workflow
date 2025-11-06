package com.bank.workflow.app.definition.query;

import lombok.Data;

/**
 * 流程定义查询参数
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessDefinitionQuery {
    
    /**
     * 流程定义KEY
     */
    private String processKey;
    
    /**
     * 流程名称（模糊查询）
     */
    private String processName;
    
    /**
     * 流程分类
     */
    private String category;
    
    /**
     * 是否挂起（null=全部，true=仅挂起，false=仅激活）
     */
    private Boolean suspended;
    
    /**
     * 是否只查询最新版本
     */
    private Boolean latestVersion;
    
    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 排序字段（deploymentTime, version）
     */
    private String orderBy = "deploymentTime";
    
    /**
     * 排序方向（asc, desc）
     */
    private String orderDirection = "desc";
}

