package com.bank.workflow.domain.comment.gateway;

import com.bank.workflow.domain.comment.entity.TaskComment;

import java.util.List;

/**
 * 任务意见网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface TaskCommentGateway {
    
    /**
     * 创建任务意见
     *
     * @param comment 任务意见
     * @return 意见ID
     */
    Long createComment(TaskComment comment);
    
    /**
     * 删除任务意见
     *
     * @param commentId 意见ID
     */
    void deleteComment(Long commentId);
    
    /**
     * 查询任务意见列表
     *
     * @param taskId 任务ID
     * @return 意见列表
     */
    List<TaskComment> listCommentsByTaskId(String taskId);
    
    /**
     * 查询流程实例的所有意见
     *
     * @param processInstanceId 流程实例ID
     * @return 意见列表
     */
    List<TaskComment> listCommentsByProcessInstanceId(String processInstanceId);
}
