package com.bank.workflow.adapter.web;

import com.bank.workflow.app.comment.TaskCommentAppService;
import com.bank.workflow.app.comment.command.CreateCommentCmd;
import com.bank.workflow.domain.comment.entity.TaskComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务意见控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/task/comment")
@RequiredArgsConstructor
public class TaskCommentController {
    
    private final TaskCommentAppService taskCommentAppService;
    
    /**
     * 创建任务意见
     */
    @PostMapping
    public Map<String, Object> createComment(@RequestBody CreateCommentCmd cmd) {
        log.info("接收创建任务意见请求: cmd={}", cmd);
        
        Long commentId = taskCommentAppService.createComment(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", Map.of("commentId", commentId));
        return result;
    }
    
    /**
     * 删除任务意见
     */
    @DeleteMapping("/{commentId}")
    public Map<String, Object> deleteComment(@PathVariable Long commentId) {
        log.info("接收删除任务意见请求: commentId={}", commentId);
        
        taskCommentAppService.deleteComment(commentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 查询任务意见列表
     */
    @GetMapping("/task/{taskId}")
    public Map<String, Object> listCommentsByTaskId(@PathVariable String taskId) {
        log.info("接收查询任务意见列表请求: taskId={}", taskId);
        
        List<TaskComment> comments = taskCommentAppService.listCommentsByTaskId(taskId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", comments);
        return result;
    }
    
    /**
     * 查询流程实例的所有意见
     */
    @GetMapping("/process/{processInstanceId}")
    public Map<String, Object> listCommentsByProcessInstanceId(@PathVariable String processInstanceId) {
        log.info("接收查询流程实例意见列表请求: processInstanceId={}", processInstanceId);
        
        List<TaskComment> comments = taskCommentAppService.listCommentsByProcessInstanceId(processInstanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", comments);
        return result;
    }
}
