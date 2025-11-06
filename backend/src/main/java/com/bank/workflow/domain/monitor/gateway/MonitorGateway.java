package com.bank.workflow.domain.monitor.gateway;

import com.bank.workflow.domain.monitor.entity.MonitorStatistics;
import com.bank.workflow.domain.monitor.entity.ProcessMonitor;
import com.bank.workflow.domain.monitor.entity.TaskMonitor;

import java.util.List;

/**
 * 监控网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface MonitorGateway {
    
    /**
     * 查询运行中的流程列表
     *
     * @return 流程监控列表
     */
    List<ProcessMonitor> listRunningProcesses();
    
    /**
     * 查询待办任务列表
     *
     * @return 任务监控列表
     */
    List<TaskMonitor> listPendingTasks();
    
    /**
     * 查询超时任务列表
     *
     * @return 超时任务列表
     */
    List<TaskMonitor> listTimeoutTasks();
    
    /**
     * 查询异常流程列表
     *
     * @return 异常流程列表
     */
    List<ProcessMonitor> listExceptionProcesses();
    
    /**
     * 获取监控统计数据
     *
     * @return 统计数据
     */
    MonitorStatistics getStatistics();
    
    /**
     * 根据流程定义Key查询运行中的流程
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程监控列表
     */
    List<ProcessMonitor> listRunningProcessesByKey(String processDefinitionKey);
    
    /**
     * 根据用户查询待办任务
     *
     * @param assignee 处理人
     * @return 任务监控列表
     */
    List<TaskMonitor> listPendingTasksByAssignee(String assignee);
}

