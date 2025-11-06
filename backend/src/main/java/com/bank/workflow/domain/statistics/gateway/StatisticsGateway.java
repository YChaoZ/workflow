package com.bank.workflow.domain.statistics.gateway;

import com.bank.workflow.domain.statistics.entity.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计网关接口
 * 负责流程、任务、用户等维度的统计数据查询
 */
public interface StatisticsGateway {

    /**
     * 获取流程统计数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流程统计列表
     */
    List<ProcessStatistics> getProcessStatistics(String processDefinitionKey, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取任务统计数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param taskDefinitionKey 任务定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计列表
     */
    List<TaskStatistics> getTaskStatistics(String processDefinitionKey, String taskDefinitionKey, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户工作量统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户工作量列表
     */
    List<UserWorkload> getUserWorkload(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取流程时间序列数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 粒度（day/week/month）
     * @return 时间序列数据列表
     */
    List<TimeSeriesData> getProcessTimeSeries(String processDefinitionKey, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取任务时间序列数据
     *
     * @param taskDefinitionKey 任务定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 粒度（day/week/month）
     * @return 时间序列数据列表
     */
    List<TimeSeriesData> getTaskTimeSeries(String taskDefinitionKey, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取流程完成率统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 完成率数据
     */
    CompletionRate getCompletionRate(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取流程效率排行
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param topN 排行数量
     * @return 流程统计列表（按平均耗时排序）
     */
    List<ProcessStatistics> getProcessEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, int topN);

    /**
     * 获取用户效率排行
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param topN 排行数量
     * @return 用户工作量列表（按效率排序）
     */
    List<UserWorkload> getUserEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, int topN);
}

