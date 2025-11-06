package com.bank.workflow.domain.statistics.entity;

import lombok.Data;

/**
 * 时间序列数据
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class TimeSeriesData {
    
    /**
     * 时间键（格式：yyyy-MM-dd 或 yyyy-MM 或 yyyy-Www）
     */
    private String timeKey;
    
    /**
     * 启动数量
     */
    private Long started;
    
    /**
     * 完成数量
     */
    private Long completed;
    
    /**
     * 平均耗时（毫秒）
     */
    private Long avgDuration;
}

