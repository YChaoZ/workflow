package com.bank.workflow.domain.home.entity;

import lombok.Data;

/**
 * 首页统计数据实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class HomeStatistics {

    /**
     * 流程定义数量
     */
    private Long processCount;

    /**
     * 待办任务数量
     */
    private Long todoCount;

    /**
     * 已办任务数量
     */
    private Long doneCount;

    /**
     * 运行中流程数量
     */
    private Long runningCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 部门总数
     */
    private Long departmentCount;
}

