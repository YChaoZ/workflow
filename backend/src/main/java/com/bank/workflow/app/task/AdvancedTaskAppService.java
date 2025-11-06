package com.bank.workflow.app.task;

import com.bank.workflow.domain.task.gateway.AdvancedTaskGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 高级任务应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedTaskAppService {

    private final AdvancedTaskGateway advancedTaskGateway;

    /**
     * 任务加签
     */
    public boolean addSign(String taskId, List<String> assignees, String type) {
        log.info("任务加签，任务ID: {}, 加签人: {}, 类型: {}", taskId, assignees, type);
        return advancedTaskGateway.addSign(taskId, assignees, type);
    }

    /**
     * 任务转办
     */
    public boolean transfer(String taskId, String targetUser, String comment) {
        log.info("任务转办，任务ID: {}, 目标用户: {}", taskId, targetUser);
        return advancedTaskGateway.transfer(taskId, targetUser, comment);
    }

    /**
     * 任务委派
     */
    public boolean delegate(String taskId, String targetUser) {
        log.info("任务委派，任务ID: {}, 目标用户: {}", taskId, targetUser);
        return advancedTaskGateway.delegate(taskId, targetUser);
    }

    /**
     * 任务回退到上一节点
     */
    public boolean rejectToPrevious(String taskId, String comment) {
        log.info("任务回退到上一节点，任务ID: {}", taskId);
        return advancedTaskGateway.rejectToPrevious(taskId, comment);
    }

    /**
     * 任务回退到指定节点
     */
    public boolean rejectToNode(String taskId, String targetNodeId, String comment) {
        log.info("任务回退到指定节点，任务ID: {}, 目标节点: {}", taskId, targetNodeId);
        return advancedTaskGateway.rejectToNode(taskId, targetNodeId, comment);
    }

    /**
     * 任务回退到流程发起人
     */
    public boolean rejectToStart(String taskId, String comment) {
        log.info("任务回退到流程发起人，任务ID: {}", taskId);
        return advancedTaskGateway.rejectToStart(taskId, comment);
    }

    /**
     * 撤回流程
     */
    public boolean withdrawProcess(String processInstanceId, String reason) {
        log.info("撤回流程，流程实例ID: {}", processInstanceId);
        return advancedTaskGateway.withdrawProcess(processInstanceId, reason);
    }

    /**
     * 撤回任务
     */
    public boolean withdrawTask(String taskId, String reason) {
        log.info("撤回任务，任务ID: {}", taskId);
        return advancedTaskGateway.withdrawTask(taskId, reason);
    }

    /**
     * 获取可回退的节点列表
     */
    public List<Map<String, Object>> getRejectableNodes(String taskId) {
        log.info("获取可回退节点，任务ID: {}", taskId);
        return advancedTaskGateway.getRejectableNodes(taskId);
    }

    /**
     * 获取历史审批人
     */
    public List<String> getHistoricAssignees(String taskId) {
        log.info("获取历史审批人，任务ID: {}", taskId);
        return advancedTaskGateway.getHistoricAssignees(taskId);
    }
}

