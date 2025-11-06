package com.bank.workflow.domain.task.gateway;

import java.util.List;
import java.util.Map;

/**
 * 高级任务操作网关接口
 */
public interface AdvancedTaskGateway {

    /**
     * 任务加签
     *
     * @param taskId 任务ID
     * @param assignees 加签人列表
     * @param type 加签类型：AND-会签, OR-或签
     * @return 是否成功
     */
    boolean addSign(String taskId, List<String> assignees, String type);

    /**
     * 任务转办
     *
     * @param taskId 任务ID
     * @param targetUser 目标用户
     * @param comment 转办说明
     * @return 是否成功
     */
    boolean transfer(String taskId, String targetUser, String comment);

    /**
     * 任务委派
     *
     * @param taskId 任务ID
     * @param targetUser 目标用户
     * @return 是否成功
     */
    boolean delegate(String taskId, String targetUser);

    /**
     * 任务回退到上一节点
     *
     * @param taskId 任务ID
     * @param comment 回退原因
     * @return 是否成功
     */
    boolean rejectToPrevious(String taskId, String comment);

    /**
     * 任务回退到指定节点
     *
     * @param taskId 任务ID
     * @param targetNodeId 目标节点ID
     * @param comment 回退原因
     * @return 是否成功
     */
    boolean rejectToNode(String taskId, String targetNodeId, String comment);

    /**
     * 任务回退到流程发起人
     *
     * @param taskId 任务ID
     * @param comment 回退原因
     * @return 是否成功
     */
    boolean rejectToStart(String taskId, String comment);

    /**
     * 撤回流程（发起人）
     *
     * @param processInstanceId 流程实例ID
     * @param reason 撤回原因
     * @return 是否成功
     */
    boolean withdrawProcess(String processInstanceId, String reason);

    /**
     * 撤回任务（审批人撤回已完成的审批）
     *
     * @param taskId 任务ID
     * @param reason 撤回原因
     * @return 是否成功
     */
    boolean withdrawTask(String taskId, String reason);

    /**
     * 获取可回退的节点列表
     *
     * @param taskId 任务ID
     * @return 可回退的节点列表
     */
    List<Map<String, Object>> getRejectableNodes(String taskId);

    /**
     * 获取任务的历史审批人
     *
     * @param taskId 任务ID
     * @return 历史审批人列表
     */
    List<String> getHistoricAssignees(String taskId);
}

