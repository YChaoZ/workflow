package com.bank.workflow.domain.task.gateway;

import com.bank.workflow.domain.task.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 任务网关接口
 * 由 Infrastructure 层实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface TaskGateway {
    
    /**
     * 查询任务
     *
     * @param taskId 任务ID
     * @return 任务
     */
    Task getTask(String taskId);
    
    /**
     * 查询任务列表
     *
     * @param params 查询参数
     * @return 任务列表
     */
    List<Task> queryTasks(Map<String, Object> params);
    
    /**
     * 统计任务数量
     *
     * @param params 查询参数
     * @return 任务数量
     */
    Long countTasks(Map<String, Object> params);
    
    /**
     * 完成任务
     *
     * @param taskId 任务ID
     * @param variables 任务变量
     * @param assignee 办理人
     */
    void completeTask(String taskId, Map<String, Object> variables, String assignee);
    
    /**
     * 认领任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    void claimTask(String taskId, String userId);
    
    /**
     * 委派任务
     *
     * @param taskId 任务ID
     * @param delegateUserId 被委派人ID
     */
    void delegateTask(String taskId, String delegateUserId);
    
    /**
     * 转办任务
     *
     * @param taskId 任务ID
     * @param targetUserId 目标用户ID
     */
    void transferTask(String taskId, String targetUserId);
    
    /**
     * 退回任务
     *
     * @param taskId 任务ID
     * @param targetNodeId 目标节点ID
     */
    void rejectTask(String taskId, String targetNodeId);
    
    /**
     * 加签任务（添加会签人员）
     *
     * @param taskId 任务ID
     * @param addUserIds 要添加的用户ID列表
     */
    void addSign(String taskId, List<String> addUserIds);
    
    /**
     * 解决任务（完成委派）
     *
     * @param taskId 任务ID
     */
    void resolveTask(String taskId);
}

