package com.bank.workflow.app.monitor;

import com.bank.workflow.domain.monitor.entity.MonitorStatistics;
import com.bank.workflow.domain.monitor.entity.ProcessMonitor;
import com.bank.workflow.domain.monitor.entity.TaskMonitor;
import com.bank.workflow.domain.monitor.gateway.MonitorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 监控应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorAppService {
    
    private final MonitorGateway monitorGateway;
    
    /**
     * 查询运行中的流程列表
     *
     * @return 流程监控列表
     */
    public List<ProcessMonitor> listRunningProcesses() {
        log.info("应用服务：查询运行中的流程列表");
        return monitorGateway.listRunningProcesses();
    }
    
    /**
     * 查询待办任务列表
     *
     * @return 任务监控列表
     */
    public List<TaskMonitor> listPendingTasks() {
        log.info("应用服务：查询待办任务列表");
        return monitorGateway.listPendingTasks();
    }
    
    /**
     * 查询超时任务列表
     *
     * @return 超时任务列表
     */
    public List<TaskMonitor> listTimeoutTasks() {
        log.info("应用服务：查询超时任务列表");
        return monitorGateway.listTimeoutTasks();
    }
    
    /**
     * 查询异常流程列表
     *
     * @return 异常流程列表
     */
    public List<ProcessMonitor> listExceptionProcesses() {
        log.info("应用服务：查询异常流程列表");
        return monitorGateway.listExceptionProcesses();
    }
    
    /**
     * 获取监控统计数据
     *
     * @return 统计数据
     */
    public MonitorStatistics getStatistics() {
        log.info("应用服务：获取监控统计数据");
        return monitorGateway.getStatistics();
    }
    
    /**
     * 根据流程定义Key查询运行中的流程
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程监控列表
     */
    public List<ProcessMonitor> listRunningProcessesByKey(String processDefinitionKey) {
        log.info("应用服务：查询流程定义Key={}的运行中流程", processDefinitionKey);
        return monitorGateway.listRunningProcessesByKey(processDefinitionKey);
    }
    
    /**
     * 根据用户查询待办任务
     *
     * @param assignee 处理人
     * @return 任务监控列表
     */
    public List<TaskMonitor> listPendingTasksByAssignee(String assignee) {
        log.info("应用服务：查询用户{}的待办任务", assignee);
        return monitorGateway.listPendingTasksByAssignee(assignee);
    }
}

