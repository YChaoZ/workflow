package com.bank.workflow.app.statistics;

import com.bank.workflow.domain.statistics.entity.*;
import com.bank.workflow.domain.statistics.gateway.StatisticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsAppService {

    private final StatisticsGateway statisticsGateway;

    /**
     * 获取流程统计数据
     */
    public List<ProcessStatistics> getProcessStatistics(String processDefinitionKey, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取流程统计数据，流程Key: {}, 开始时间: {}, 结束时间: {}", processDefinitionKey, startTime, endTime);
        return statisticsGateway.getProcessStatistics(processDefinitionKey, startTime, endTime);
    }

    /**
     * 获取任务统计数据
     */
    public List<TaskStatistics> getTaskStatistics(String processDefinitionKey, String taskDefinitionKey, 
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取任务统计数据，流程Key: {}, 任务Key: {}, 开始时间: {}, 结束时间: {}", 
                processDefinitionKey, taskDefinitionKey, startTime, endTime);
        return statisticsGateway.getTaskStatistics(processDefinitionKey, taskDefinitionKey, startTime, endTime);
    }

    /**
     * 获取用户工作量统计
     */
    public List<UserWorkload> getUserWorkload(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取用户工作量统计，开始时间: {}, 结束时间: {}", startTime, endTime);
        return statisticsGateway.getUserWorkload(startTime, endTime);
    }

    /**
     * 获取流程时间序列数据
     */
    public List<TimeSeriesData> getProcessTimeSeries(String processDefinitionKey, LocalDateTime startTime, 
                                                      LocalDateTime endTime, String granularity) {
        log.info("获取流程时间序列数据，流程Key: {}, 粒度: {}", processDefinitionKey, granularity);
        
        // 默认粒度为day
        if (granularity == null || granularity.isEmpty()) {
            granularity = "day";
        }
        
        return statisticsGateway.getProcessTimeSeries(processDefinitionKey, startTime, endTime, granularity);
    }

    /**
     * 获取任务时间序列数据
     */
    public List<TimeSeriesData> getTaskTimeSeries(String taskDefinitionKey, LocalDateTime startTime, 
                                                   LocalDateTime endTime, String granularity) {
        log.info("获取任务时间序列数据，任务Key: {}, 粒度: {}", taskDefinitionKey, granularity);
        
        // 默认粒度为day
        if (granularity == null || granularity.isEmpty()) {
            granularity = "day";
        }
        
        return statisticsGateway.getTaskTimeSeries(taskDefinitionKey, startTime, endTime, granularity);
    }

    /**
     * 获取流程完成率统计
     */
    public CompletionRate getCompletionRate(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取流程完成率统计，开始时间: {}, 结束时间: {}", startTime, endTime);
        return statisticsGateway.getCompletionRate(startTime, endTime);
    }

    /**
     * 获取流程效率排行
     */
    public List<ProcessStatistics> getProcessEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, Integer topN) {
        log.info("获取流程效率排行，TOP: {}", topN);
        
        // 默认TOP 10
        if (topN == null || topN <= 0) {
            topN = 10;
        }
        
        return statisticsGateway.getProcessEfficiencyRanking(startTime, endTime, topN);
    }

    /**
     * 获取用户效率排行
     */
    public List<UserWorkload> getUserEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, Integer topN) {
        log.info("获取用户效率排行，TOP: {}", topN);
        
        // 默认TOP 10
        if (topN == null || topN <= 0) {
            topN = 10;
        }
        
        return statisticsGateway.getUserEfficiencyRanking(startTime, endTime, topN);
    }
}

