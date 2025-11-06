package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.statistics.entity.*;
import com.bank.workflow.domain.statistics.gateway.StatisticsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计网关实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsGatewayImpl implements StatisticsGateway {

    private final HistoryService historyService;

    @Override
    public List<ProcessStatistics> getProcessStatistics(String processDefinitionKey, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询
        var query = historyService.createHistoricProcessInstanceQuery();
        
        if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
            query.processDefinitionKey(processDefinitionKey);
        }
        
        if (startTime != null) {
            query.startedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.startedBefore(convertToDate(endTime));
        }
        
        List<HistoricProcessInstance> instances = query.list();
        
        // 按流程定义分组统计
        Map<String, List<HistoricProcessInstance>> groupedByKey = instances.stream()
                .collect(Collectors.groupingBy(HistoricProcessInstance::getProcessDefinitionKey));
        
        return groupedByKey.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    List<HistoricProcessInstance> list = entry.getValue();
                    
                    ProcessStatistics stats = new ProcessStatistics();
                    stats.setProcessDefinitionKey(key);
                    stats.setProcessDefinitionName(list.get(0).getProcessDefinitionName());
                    stats.setTotalStarted((long) list.size());
                    
                    long completed = list.stream().filter(i -> i.getEndTime() != null).count();
                    long running = list.size() - completed;
                    
                    stats.setTotalCompleted(completed);
                    stats.setTotalRunning(running);
                    
                    // 计算平均耗时（已完成的）
                    List<HistoricProcessInstance> completedInstances = list.stream()
                            .filter(i -> i.getEndTime() != null)
                            .toList();
                    
                    if (!completedInstances.isEmpty()) {
                        double avgDuration = completedInstances.stream()
                                .mapToLong(HistoricProcessInstance::getDurationInMillis)
                                .average()
                                .orElse(0.0);
                        stats.setAvgDuration((long) avgDuration);
                        
                        // 最长和最短耗时
                        stats.setMaxDuration(completedInstances.stream()
                                .mapToLong(HistoricProcessInstance::getDurationInMillis)
                                .max()
                                .orElse(0L));
                        
                        stats.setMinDuration(completedInstances.stream()
                                .mapToLong(HistoricProcessInstance::getDurationInMillis)
                                .min()
                                .orElse(0L));
                    }
                    
                    return stats;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskStatistics> getTaskStatistics(String processDefinitionKey, String taskDefinitionKey, 
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询
        var query = historyService.createHistoricTaskInstanceQuery();
        
        if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
            query.processDefinitionKey(processDefinitionKey);
        }
        
        if (taskDefinitionKey != null && !taskDefinitionKey.isEmpty()) {
            query.taskDefinitionKey(taskDefinitionKey);
        }
        
        if (startTime != null) {
            query.taskCreatedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.taskCreatedBefore(convertToDate(endTime));
        }
        
        List<HistoricTaskInstance> tasks = query.list();
        
        // 按任务定义分组统计
        Map<String, List<HistoricTaskInstance>> groupedByKey = tasks.stream()
                .filter(t -> t.getTaskDefinitionKey() != null)
                .collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey));
        
        return groupedByKey.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    List<HistoricTaskInstance> list = entry.getValue();
                    
                    TaskStatistics stats = new TaskStatistics();
                    stats.setTaskDefinitionKey(key);
                    stats.setTaskName(list.get(0).getName());
                    
                    // 从历史任务实例获取流程定义ID，然后查询流程定义获取key
                    if (!list.isEmpty() && list.get(0).getProcessInstanceId() != null) {
                        var processInstance = historyService.createHistoricProcessInstanceQuery()
                                .processInstanceId(list.get(0).getProcessInstanceId())
                                .singleResult();
                        if (processInstance != null) {
                            stats.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
                        }
                    }
                    
                    stats.setTotalTasks((long) list.size());
                    
                    long completed = list.stream().filter(t -> t.getEndTime() != null).count();
                    long pending = list.size() - completed;
                    
                    stats.setCompletedTasks(completed);
                    stats.setPendingTasks(pending);
                    
                    // 计算平均耗时（已完成的）
                    List<HistoricTaskInstance> completedTasks = list.stream()
                            .filter(t -> t.getEndTime() != null && t.getDurationInMillis() != null)
                            .toList();
                    
                    if (!completedTasks.isEmpty()) {
                        double avgDuration = completedTasks.stream()
                                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                                .average()
                                .orElse(0.0);
                        stats.setAvgDuration((long) avgDuration);
                        
                        // 最长和最短耗时
                        stats.setMaxDuration(completedTasks.stream()
                                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                                .max()
                                .orElse(0L));
                        
                        stats.setMinDuration(completedTasks.stream()
                                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                                .min()
                                .orElse(0L));
                    }
                    
                    return stats;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserWorkload> getUserWorkload(LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询
        var query = historyService.createHistoricTaskInstanceQuery()
                .finished();
        
        if (startTime != null) {
            query.taskCompletedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.taskCompletedBefore(convertToDate(endTime));
        }
        
        List<HistoricTaskInstance> tasks = query.list();
        
        // 按处理人分组统计
        Map<String, List<HistoricTaskInstance>> groupedByAssignee = tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(HistoricTaskInstance::getAssignee));
        
        return groupedByAssignee.entrySet().stream()
                .map(entry -> {
                    String assignee = entry.getKey();
                    List<HistoricTaskInstance> list = entry.getValue();
                    
                    UserWorkload workload = new UserWorkload();
                    workload.setUserId(assignee);
                    workload.setUsername(assignee); // TODO: 从用户服务获取用户名
                    workload.setTotalTasks((long) list.size());
                    
                    // 计算总耗时和平均耗时
                    long totalDuration = list.stream()
                            .filter(t -> t.getDurationInMillis() != null)
                            .mapToLong(HistoricTaskInstance::getDurationInMillis)
                            .sum();
                    
                    workload.setTotalDuration(totalDuration);
                    
                    if (!list.isEmpty()) {
                        double avgDuration = list.stream()
                                .filter(t -> t.getDurationInMillis() != null)
                                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                                .average()
                                .orElse(0.0);
                        workload.setAvgTaskDuration((long) avgDuration);
                    }
                    
                    return workload;
                })
                .sorted(Comparator.comparing(UserWorkload::getTotalTasks).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesData> getProcessTimeSeries(String processDefinitionKey, LocalDateTime startTime, 
                                                      LocalDateTime endTime, String granularity) {
        // 构建查询
        var query = historyService.createHistoricProcessInstanceQuery();
        
        if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
            query.processDefinitionKey(processDefinitionKey);
        }
        
        if (startTime != null) {
            query.startedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.startedBefore(convertToDate(endTime));
        }
        
        List<HistoricProcessInstance> instances = query.list();
        
        // 按时间粒度分组
        Map<String, List<HistoricProcessInstance>> groupedByTime = instances.stream()
                .collect(Collectors.groupingBy(instance -> {
                    LocalDateTime time = convertToLocalDateTime(instance.getStartTime());
                    return formatTimeByGranularity(time, granularity);
                }));
        
        return groupedByTime.entrySet().stream()
                .map(entry -> {
                    String timeKey = entry.getKey();
                    List<HistoricProcessInstance> list = entry.getValue();
                    
                    TimeSeriesData data = new TimeSeriesData();
                    data.setTimeKey(timeKey);
                    data.setStarted((long) list.size());
                    data.setCompleted(list.stream().filter(i -> i.getEndTime() != null).count());
                    
                    // 计算平均耗时
                    List<HistoricProcessInstance> completedInstances = list.stream()
                            .filter(i -> i.getEndTime() != null)
                            .toList();
                    
                    if (!completedInstances.isEmpty()) {
                        double avgDuration = completedInstances.stream()
                                .mapToLong(HistoricProcessInstance::getDurationInMillis)
                                .average()
                                .orElse(0.0);
                        data.setAvgDuration((long) avgDuration);
                    }
                    
                    return data;
                })
                .sorted(Comparator.comparing(TimeSeriesData::getTimeKey))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesData> getTaskTimeSeries(String taskDefinitionKey, LocalDateTime startTime, 
                                                   LocalDateTime endTime, String granularity) {
        // 构建查询
        var query = historyService.createHistoricTaskInstanceQuery();
        
        if (taskDefinitionKey != null && !taskDefinitionKey.isEmpty()) {
            query.taskDefinitionKey(taskDefinitionKey);
        }
        
        if (startTime != null) {
            query.taskCreatedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.taskCreatedBefore(convertToDate(endTime));
        }
        
        List<HistoricTaskInstance> tasks = query.list();
        
        // 按时间粒度分组
        Map<String, List<HistoricTaskInstance>> groupedByTime = tasks.stream()
                .collect(Collectors.groupingBy(task -> {
                    LocalDateTime time = convertToLocalDateTime(task.getCreateTime());
                    return formatTimeByGranularity(time, granularity);
                }));
        
        return groupedByTime.entrySet().stream()
                .map(entry -> {
                    String timeKey = entry.getKey();
                    List<HistoricTaskInstance> list = entry.getValue();
                    
                    TimeSeriesData data = new TimeSeriesData();
                    data.setTimeKey(timeKey);
                    data.setStarted((long) list.size());
                    data.setCompleted(list.stream().filter(t -> t.getEndTime() != null).count());
                    
                    // 计算平均耗时
                    List<HistoricTaskInstance> completedTasks = list.stream()
                            .filter(t -> t.getEndTime() != null && t.getDurationInMillis() != null)
                            .toList();
                    
                    if (!completedTasks.isEmpty()) {
                        double avgDuration = completedTasks.stream()
                                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                                .average()
                                .orElse(0.0);
                        data.setAvgDuration((long) avgDuration);
                    }
                    
                    return data;
                })
                .sorted(Comparator.comparing(TimeSeriesData::getTimeKey))
                .collect(Collectors.toList());
    }

    @Override
    public CompletionRate getCompletionRate(LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询
        var query = historyService.createHistoricProcessInstanceQuery();
        
        if (startTime != null) {
            query.startedAfter(convertToDate(startTime));
        }
        
        if (endTime != null) {
            query.startedBefore(convertToDate(endTime));
        }
        
        List<HistoricProcessInstance> instances = query.list();
        
        CompletionRate rate = new CompletionRate();
        rate.setTotalStarted((long) instances.size());
        
        long completed = instances.stream().filter(i -> i.getEndTime() != null && i.getDeleteReason() == null).count();
        long terminated = instances.stream().filter(i -> i.getDeleteReason() != null).count();
        long running = instances.size() - completed - terminated;
        
        rate.setTotalCompleted(completed);
        rate.setTotalTerminated(terminated);
        rate.setTotalRunning(running);
        
        rate.calculateRates();
        
        return rate;
    }

    @Override
    public List<ProcessStatistics> getProcessEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, int topN) {
        List<ProcessStatistics> statistics = getProcessStatistics(null, startTime, endTime);
        
        // 按平均耗时排序（从短到长）
        return statistics.stream()
                .filter(s -> s.getAvgDuration() != null && s.getAvgDuration() > 0)
                .sorted(Comparator.comparing(ProcessStatistics::getAvgDuration))
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserWorkload> getUserEfficiencyRanking(LocalDateTime startTime, LocalDateTime endTime, int topN) {
        List<UserWorkload> workloads = getUserWorkload(startTime, endTime);
        
        // 按平均任务耗时排序（从短到长）
        return workloads.stream()
                .filter(w -> w.getAvgTaskDuration() != null && w.getAvgTaskDuration() > 0)
                .sorted(Comparator.comparing(UserWorkload::getAvgTaskDuration))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * 将LocalDateTime转换为Date
     */
    private Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将Date转换为LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 根据粒度格式化时间
     */
    private String formatTimeByGranularity(LocalDateTime time, String granularity) {
        if (time == null) {
            return "";
        }
        
        return switch (granularity.toLowerCase()) {
            case "day" -> time.truncatedTo(ChronoUnit.DAYS).toString().substring(0, 10);
            case "week" -> {
                // 获取周的第一天
                LocalDateTime weekStart = time.truncatedTo(ChronoUnit.DAYS)
                        .minusDays(time.getDayOfWeek().getValue() - 1);
                yield weekStart.toString().substring(0, 10);
            }
            case "month" -> time.toString().substring(0, 7);
            default -> time.truncatedTo(ChronoUnit.DAYS).toString().substring(0, 10);
        };
    }
}

