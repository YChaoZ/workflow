package com.bank.workflow.adapter.web;

import com.bank.workflow.app.statistics.StatisticsAppService;
import com.bank.workflow.domain.statistics.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsAppService statisticsAppService;

    /**
     * 获取流程统计数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 流程统计列表
     */
    @GetMapping("/process")
    public Map<String, Object> getProcessStatistics(
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        log.info("获取流程统计数据，流程Key: {}, 开始时间: {}, 结束时间: {}", processDefinitionKey, startTime, endTime);
        
        List<ProcessStatistics> statistics = statisticsAppService.getProcessStatistics(processDefinitionKey, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", statistics);
        return result;
    }

    /**
     * 获取任务统计数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param taskDefinitionKey 任务定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计列表
     */
    @GetMapping("/task")
    public Map<String, Object> getTaskStatistics(
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) String taskDefinitionKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        log.info("获取任务统计数据，流程Key: {}, 任务Key: {}, 开始时间: {}, 结束时间: {}", 
                processDefinitionKey, taskDefinitionKey, startTime, endTime);
        
        List<TaskStatistics> statistics = statisticsAppService.getTaskStatistics(
                processDefinitionKey, taskDefinitionKey, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", statistics);
        return result;
    }

    /**
     * 获取用户工作量统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户工作量列表
     */
    @GetMapping("/user/workload")
    public Map<String, Object> getUserWorkload(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        log.info("获取用户工作量统计，开始时间: {}, 结束时间: {}", startTime, endTime);
        
        List<UserWorkload> workloads = statisticsAppService.getUserWorkload(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", workloads);
        return result;
    }

    /**
     * 获取流程时间序列数据
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 粒度（day/week/month）
     * @return 时间序列数据列表
     */
    @GetMapping("/process/timeseries")
    public Map<String, Object> getProcessTimeSeries(
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String granularity) {
        
        log.info("获取流程时间序列数据，流程Key: {}, 粒度: {}", processDefinitionKey, granularity);
        
        List<TimeSeriesData> data = statisticsAppService.getProcessTimeSeries(
                processDefinitionKey, startTime, endTime, granularity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    /**
     * 获取任务时间序列数据
     *
     * @param taskDefinitionKey 任务定义Key（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 粒度（day/week/month）
     * @return 时间序列数据列表
     */
    @GetMapping("/task/timeseries")
    public Map<String, Object> getTaskTimeSeries(
            @RequestParam(required = false) String taskDefinitionKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String granularity) {
        
        log.info("获取任务时间序列数据，任务Key: {}, 粒度: {}", taskDefinitionKey, granularity);
        
        List<TimeSeriesData> data = statisticsAppService.getTaskTimeSeries(
                taskDefinitionKey, startTime, endTime, granularity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    /**
     * 获取流程完成率统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 完成率数据
     */
    @GetMapping("/completion-rate")
    public Map<String, Object> getCompletionRate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        log.info("获取流程完成率统计，开始时间: {}, 结束时间: {}", startTime, endTime);
        
        CompletionRate rate = statisticsAppService.getCompletionRate(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", rate);
        return result;
    }

    /**
     * 获取流程效率排行
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param topN 排行数量（默认10）
     * @return 流程统计列表
     */
    @GetMapping("/process/efficiency-ranking")
    public Map<String, Object> getProcessEfficiencyRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") Integer topN) {
        
        log.info("获取流程效率排行，TOP: {}", topN);
        
        List<ProcessStatistics> statistics = statisticsAppService.getProcessEfficiencyRanking(startTime, endTime, topN);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", statistics);
        return result;
    }

    /**
     * 获取用户效率排行
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param topN 排行数量（默认10）
     * @return 用户工作量列表
     */
    @GetMapping("/user/efficiency-ranking")
    public Map<String, Object> getUserEfficiencyRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") Integer topN) {
        
        log.info("获取用户效率排行，TOP: {}", topN);
        
        List<UserWorkload> workloads = statisticsAppService.getUserEfficiencyRanking(startTime, endTime, topN);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", workloads);
        return result;
    }
}

