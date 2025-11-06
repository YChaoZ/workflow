package com.bank.workflow.domain.statistics.entity;

import lombok.Data;

/**
 * 完成率统计实体
 */
@Data
public class CompletionRate {

    /**
     * 启动总数
     */
    private Long totalStarted;

    /**
     * 完成总数
     */
    private Long totalCompleted;

    /**
     * 运行中数量
     */
    private Long totalRunning;

    /**
     * 已终止数量
     */
    private Long totalTerminated;

    /**
     * 完成率（百分比）
     */
    private Double completionRate;

    /**
     * 终止率（百分比）
     */
    private Double terminationRate;

    /**
     * 计算完成率
     */
    public void calculateRates() {
        if (totalStarted > 0) {
            this.completionRate = (totalCompleted * 100.0) / totalStarted;
            this.terminationRate = (totalTerminated * 100.0) / totalStarted;
        } else {
            this.completionRate = 0.0;
            this.terminationRate = 0.0;
        }
    }
}

