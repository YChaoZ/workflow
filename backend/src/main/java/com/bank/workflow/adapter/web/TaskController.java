package com.bank.workflow.adapter.web;

import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.task.TaskAppService;
import com.bank.workflow.app.task.command.AddSignCmd;
import com.bank.workflow.app.task.command.CompleteTaskCmd;
import com.bank.workflow.app.task.command.RejectTaskCmd;
import com.bank.workflow.app.task.query.TaskQuery;
import com.bank.workflow.domain.task.entity.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskAppService taskAppService;
    
    /**
     * 分页查询任务列表
     */
    @GetMapping("/list")
    public Map<String, Object> queryTasks(TaskQuery query) {
        log.info("接收查询任务列表请求: query={}", query);
        
        PageResult<Task> pageResult = taskAppService.queryTasks(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }
    
    /**
     * 查询单个任务
     */
    @GetMapping("/{taskId}")
    public Map<String, Object> getTask(@PathVariable String taskId) {
        log.info("接收查询任务请求: taskId={}", taskId);
        
        Task task = taskAppService.getTask(taskId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", task);
        return result;
    }
    
    /**
     * 完成任务
     */
    @PostMapping("/complete")
    public Map<String, Object> completeTask(@RequestBody CompleteTaskCmd cmd) {
        log.info("接收完成任务请求: taskId={}", cmd.getTaskId());
        
        taskAppService.completeTask(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已完成");
        return result;
    }
    
    /**
     * 认领任务
     */
    @PostMapping("/{taskId}/claim")
    public Map<String, Object> claimTask(
            @PathVariable String taskId, 
            @RequestParam String userId) {
        log.info("接收认领任务请求: taskId={}, userId={}", taskId, userId);
        
        taskAppService.claimTask(taskId, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已认领");
        return result;
    }
    
    /**
     * 委派任务
     */
    @PostMapping("/{taskId}/delegate")
    public Map<String, Object> delegateTask(
            @PathVariable String taskId,
            @RequestParam String delegateUserId) {
        log.info("接收委派任务请求: taskId={}, delegateUserId={}", taskId, delegateUserId);
        
        taskAppService.delegateTask(taskId, delegateUserId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已委派");
        return result;
    }
    
    /**
     * 转办任务
     */
    @PostMapping("/{taskId}/transfer")
    public Map<String, Object> transferTask(
            @PathVariable String taskId,
            @RequestParam String targetUserId) {
        log.info("接收转办任务请求: taskId={}, targetUserId={}", taskId, targetUserId);
        
        taskAppService.transferTask(taskId, targetUserId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已转办");
        return result;
    }
    
    /**
     * 加签任务
     */
    @PostMapping("/{taskId}/addSign")
    public Map<String, Object> addSign(
            @PathVariable String taskId,
            @RequestBody AddSignCmd cmd) {
        log.info("接收加签任务请求: taskId={}, cmd={}", taskId, cmd);
        
        cmd.setTaskId(taskId);
        taskAppService.addSign(cmd.getTaskId(), cmd.getAddUserIds());
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "加签成功");
        return result;
    }
    
    /**
     * 退回任务
     */
    @PostMapping("/{taskId}/reject")
    public Map<String, Object> rejectTask(
            @PathVariable String taskId,
            @RequestBody RejectTaskCmd cmd) {
        log.info("接收退回任务请求: taskId={}, cmd={}", taskId, cmd);
        
        cmd.setTaskId(taskId);
        taskAppService.rejectTask(cmd.getTaskId(), cmd.getTargetNodeId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已退回");
        return result;
    }
    
    /**
     * 解决任务（完成委派）
     */
    @PostMapping("/{taskId}/resolve")
    public Map<String, Object> resolveTask(@PathVariable String taskId) {
        log.info("接收解决任务请求: taskId={}", taskId);
        
        taskAppService.resolveTask(taskId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "任务已解决");
        return result;
    }
}

