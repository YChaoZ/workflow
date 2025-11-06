package com.bank.workflow.adapter.web;

import com.bank.workflow.app.monitor.MonitorAppService;
import com.bank.workflow.domain.monitor.entity.MonitorStatistics;
import com.bank.workflow.domain.monitor.entity.ProcessMonitor;
import com.bank.workflow.domain.monitor.entity.TaskMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final MonitorAppService monitorAppService;
    
    /**
     * 获取监控统计数据
     */
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        log.info("接收监控统计数据查询请求");
        
        MonitorStatistics statistics = monitorAppService.getStatistics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", statistics);
        return result;
    }
    
    /**
     * 查询运行中的流程列表
     */
    @GetMapping("/processes/running")
    public Map<String, Object> listRunningProcesses(
            @RequestParam(required = false) String processDefinitionKey) {
        log.info("接收运行中流程列表查询请求: processDefinitionKey={}", processDefinitionKey);
        
        List<ProcessMonitor> processes;
        if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
            processes = monitorAppService.listRunningProcessesByKey(processDefinitionKey);
        } else {
            processes = monitorAppService.listRunningProcesses();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", processes);
        return result;
    }
    
    /**
     * 查询异常流程列表
     */
    @GetMapping("/processes/exception")
    public Map<String, Object> listExceptionProcesses() {
        log.info("接收异常流程列表查询请求");
        
        List<ProcessMonitor> processes = monitorAppService.listExceptionProcesses();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", processes);
        return result;
    }
    
    /**
     * 查询待办任务列表
     */
    @GetMapping("/tasks/pending")
    public Map<String, Object> listPendingTasks(
            @RequestParam(required = false) String assignee) {
        log.info("接收待办任务列表查询请求: assignee={}", assignee);
        
        List<TaskMonitor> tasks;
        if (assignee != null && !assignee.isEmpty()) {
            tasks = monitorAppService.listPendingTasksByAssignee(assignee);
        } else {
            tasks = monitorAppService.listPendingTasks();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", tasks);
        return result;
    }
    
    /**
     * 查询超时任务列表
     */
    @GetMapping("/tasks/timeout")
    public Map<String, Object> listTimeoutTasks() {
        log.info("接收超时任务列表查询请求");
        
        List<TaskMonitor> tasks = monitorAppService.listTimeoutTasks();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", tasks);
        return result;
    }
}

