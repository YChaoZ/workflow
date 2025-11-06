package com.bank.workflow.app.comment;

import com.bank.workflow.app.comment.command.CreateCommentCmd;
import com.bank.workflow.domain.comment.entity.TaskComment;
import com.bank.workflow.domain.comment.gateway.TaskCommentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务意见应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCommentAppService {
    
    private final TaskCommentGateway taskCommentGateway;
    
    /**
     * 创建任务意见
     */
    public Long createComment(CreateCommentCmd cmd) {
        log.info("创建任务意见: cmd={}", cmd);
        
        // 参数校验
        if (cmd.getTaskId() == null || cmd.getTaskId().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (cmd.getCommentText() == null || cmd.getCommentText().isEmpty()) {
            throw new IllegalArgumentException("意见内容不能为空");
        }
        if (cmd.getCommentType() == null || cmd.getCommentType().isEmpty()) {
            throw new IllegalArgumentException("意见类型不能为空");
        }
        
        // 转换为领域实体
        TaskComment comment = new TaskComment();
        BeanUtils.copyProperties(cmd, comment);
        
        // 创建意见
        return taskCommentGateway.createComment(comment);
    }
    
    /**
     * 删除任务意见
     */
    public void deleteComment(Long commentId) {
        log.info("删除任务意见: commentId={}", commentId);
        
        if (commentId == null) {
            throw new IllegalArgumentException("意见ID不能为空");
        }
        
        taskCommentGateway.deleteComment(commentId);
    }
    
    /**
     * 查询任务意见列表
     */
    public List<TaskComment> listCommentsByTaskId(String taskId) {
        log.info("查询任务意见列表: taskId={}", taskId);
        
        if (taskId == null || taskId.isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        return taskCommentGateway.listCommentsByTaskId(taskId);
    }
    
    /**
     * 查询流程实例的所有意见
     */
    public List<TaskComment> listCommentsByProcessInstanceId(String processInstanceId) {
        log.info("查询流程实例意见列表: processInstanceId={}", processInstanceId);
        
        if (processInstanceId == null || processInstanceId.isEmpty()) {
            throw new IllegalArgumentException("流程实例ID不能为空");
        }
        
        return taskCommentGateway.listCommentsByProcessInstanceId(processInstanceId);
    }
}
