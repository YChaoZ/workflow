package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.monitor.entity.MonitorStatistics;
import com.bank.workflow.domain.monitor.entity.ProcessMonitor;
import com.bank.workflow.domain.monitor.entity.TaskMonitor;
import com.bank.workflow.domain.monitor.gateway.MonitorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 监控网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorGatewayImpl implements MonitorGateway {
    
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    
    @Override
    public List<ProcessMonitor> listRunningProcesses() {
        log.info("查询运行中的流程列表");
        
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .active()
                .orderByStartTime()
                .desc()
                .list();
        
        return instances.stream()
                .map(this::convertToProcessMonitor)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskMonitor> listPendingTasks() {
        log.info("查询待办任务列表");
        
        List<Task> tasks = taskService.createTaskQuery()
                .orderByTaskCreateTime()
                .desc()
                .list();
        
        return tasks.stream()
                .map(this::convertToTaskMonitor)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskMonitor> listTimeoutTasks() {
        log.info("查询超时任务列表");
        
        // 查询有到期时间且已过期的任务
        Date now = new Date();
        List<Task> tasks = taskService.createTaskQuery()
                .taskDueBefore(now)
                .orderByTaskCreateTime()
                .desc()
                .list();
        
        return tasks.stream()
                .map(task -> {
                    TaskMonitor monitor = convertToTaskMonitor(task);
                    monitor.setTimeout(true);
                    return monitor;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProcessMonitor> listExceptionProcesses() {
        log.info("查询异常流程列表");
        
        // 查询挂起的流程实例（可能表示异常）
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .suspended()
                .orderByStartTime()
                .desc()
                .list();
        
        return instances.stream()
                .map(instance -> {
                    ProcessMonitor monitor = convertToProcessMonitor(instance);
                    monitor.setException(true);
                    monitor.setExceptionReason("流程已挂起");
                    return monitor;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public MonitorStatistics getStatistics() {
        log.info("获取监控统计数据");
        
        MonitorStatistics statistics = new MonitorStatistics();
        
        // 运行中流程数量
        long runningCount = runtimeService.createProcessInstanceQuery()
                .active()
                .count();
        statistics.setRunningProcessCount(runningCount);
        
        // 挂起流程数量
        long suspendedCount = runtimeService.createProcessInstanceQuery()
                .suspended()
                .count();
        statistics.setSuspendedProcessCount(suspendedCount);
        
        // 异常流程数量（暂用挂起数量代替）
        statistics.setExceptionProcessCount(suspendedCount);
        
        // 待办任务数量
        long pendingCount = taskService.createTaskQuery().count();
        statistics.setPendingTaskCount(pendingCount);
        
        // 超时任务数量
        Date now = new Date();
        long timeoutCount = taskService.createTaskQuery()
                .taskDueBefore(now)
                .count();
        statistics.setTimeoutTaskCount(timeoutCount);
        
        // 今日启动流程数量
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        long todayStarted = historyService.createHistoricProcessInstanceQuery()
                .startedAfter(startOfDay)
                .startedBefore(endOfDay)
                .count();
        statistics.setTodayStartedCount(todayStarted);
        
        // 今日完成流程数量
        long todayCompleted = historyService.createHistoricProcessInstanceQuery()
                .finishedAfter(startOfDay)
                .finishedBefore(endOfDay)
                .count();
        statistics.setTodayCompletedCount(todayCompleted);
        
        // 平均流程耗时（最近100个已完成流程）
        List<org.flowable.engine.history.HistoricProcessInstance> recentFinished = 
                historyService.createHistoricProcessInstanceQuery()
                        .finished()
                        .orderByProcessInstanceEndTime()
                        .desc()
                        .listPage(0, 100);
        
        if (!recentFinished.isEmpty()) {
            double avgDuration = recentFinished.stream()
                    .filter(p -> p.getDurationInMillis() != null)
                    .mapToLong(org.flowable.engine.history.HistoricProcessInstance::getDurationInMillis)
                    .average()
                    .orElse(0.0);
            statistics.setAvgProcessDuration((long) avgDuration);
        } else {
            statistics.setAvgProcessDuration(0L);
        }
        
        // 平均任务耗时（最近100个已完成任务）
        List<org.flowable.task.api.history.HistoricTaskInstance> recentTasks = 
                historyService.createHistoricTaskInstanceQuery()
                        .finished()
                        .orderByHistoricTaskInstanceEndTime()
                        .desc()
                        .listPage(0, 100);
        
        if (!recentTasks.isEmpty()) {
            double avgTaskDuration = recentTasks.stream()
                    .filter(t -> t.getDurationInMillis() != null)
                    .mapToLong(org.flowable.task.api.history.HistoricTaskInstance::getDurationInMillis)
                    .average()
                    .orElse(0.0);
            statistics.setAvgTaskDuration((long) avgTaskDuration);
        } else {
            statistics.setAvgTaskDuration(0L);
        }
        
        return statistics;
    }
    
    @Override
    public List<ProcessMonitor> listRunningProcessesByKey(String processDefinitionKey) {
        log.info("查询流程定义Key={}的运行中流程", processDefinitionKey);
        
        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .active()
                .orderByStartTime()
                .desc()
                .list();
        
        return instances.stream()
                .map(this::convertToProcessMonitor)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskMonitor> listPendingTasksByAssignee(String assignee) {
        log.info("查询用户{}的待办任务", assignee);
        
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime()
                .desc()
                .list();
        
        return tasks.stream()
                .map(this::convertToTaskMonitor)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换流程实例为监控对象
     */
    private ProcessMonitor convertToProcessMonitor(ProcessInstance instance) {
        ProcessMonitor monitor = new ProcessMonitor();
        
        monitor.setProcessInstanceId(instance.getProcessInstanceId());
        monitor.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        monitor.setProcessDefinitionName(instance.getProcessDefinitionName());
        monitor.setBusinessKey(instance.getBusinessKey());
        monitor.setProcessName(instance.getName());
        monitor.setStartUser(instance.getStartUserId());
        monitor.setStartTime(convertToLocalDateTime(instance.getStartTime()));
        monitor.setStatus(instance.isSuspended() ? "suspended" : "running");
        
        // 计算运行时长
        long duration = System.currentTimeMillis() - instance.getStartTime().getTime();
        monitor.setDuration(duration);
        
        // 查询当前活动任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(instance.getProcessInstanceId())
                .list();
        
        if (!tasks.isEmpty()) {
            Task currentTask = tasks.get(0);
            monitor.setCurrentNodeName(currentTask.getName());
            monitor.setCurrentAssignee(currentTask.getAssignee());
        }
        
        // 判断是否超时（假设超过3天为超时）
        monitor.setTimeout(duration > 3 * 24 * 60 * 60 * 1000L);
        
        monitor.setException(instance.isSuspended());
        if (instance.isSuspended()) {
            monitor.setExceptionReason("流程已挂起");
        }
        
        return monitor;
    }
    
    /**
     * 转换任务为监控对象
     */
    private TaskMonitor convertToTaskMonitor(Task task) {
        TaskMonitor monitor = new TaskMonitor();
        
        monitor.setTaskId(task.getId());
        monitor.setTaskName(task.getName());
        monitor.setProcessInstanceId(task.getProcessInstanceId());
        
        // 通过流程实例获取流程定义Key
        if (task.getProcessInstanceId() != null) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (processInstance != null) {
                monitor.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
                monitor.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            }
        }
        
        monitor.setAssignee(task.getAssignee());
        monitor.setCreateTime(convertToLocalDateTime(task.getCreateTime()));
        monitor.setDueDate(convertToLocalDateTime(task.getDueDate()));
        monitor.setPriority(task.getPriority());
        monitor.setStatus("pending");
        
        // 计算等待时长
        long waitingDuration = System.currentTimeMillis() - task.getCreateTime().getTime();
        monitor.setWaitingDuration(waitingDuration);
        
        // 判断是否超时
        if (task.getDueDate() != null) {
            monitor.setTimeout(new Date().after(task.getDueDate()));
        } else {
            // 假设超过24小时为超时
            monitor.setTimeout(waitingDuration > 24 * 60 * 60 * 1000L);
        }
        
        return monitor;
    }
    
    /**
     * 转换Date为LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}

