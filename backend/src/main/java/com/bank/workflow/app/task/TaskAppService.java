package com.bank.workflow.app.task;

import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.task.command.CompleteTaskCmd;
import com.bank.workflow.app.task.query.TaskQuery;
import com.bank.workflow.domain.task.entity.Task;
import com.bank.workflow.domain.task.gateway.TaskGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAppService {
    
    private final TaskGateway taskGateway;
    
    /**
     * 查询任务
     */
    public Task getTask(String taskId) {
        return taskGateway.getTask(taskId);
    }
    
    /**
     * 分页查询任务列表
     */
    public PageResult<Task> queryTasks(TaskQuery queryParam) {
        log.info("查询任务列表: query={}", queryParam);
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("assignee", queryParam.getAssignee());
        params.put("candidateUser", queryParam.getCandidateUser());
        params.put("candidateGroup", queryParam.getCandidateGroup());
        params.put("processKey", queryParam.getProcessKey());
        params.put("processInstanceId", queryParam.getProcessInstanceId());
        params.put("businessKey", queryParam.getBusinessKey());
        params.put("taskName", queryParam.getTaskName());
        params.put("taskStatus", queryParam.getTaskStatus());
        params.put("pageNum", queryParam.getPageNum());
        params.put("pageSize", queryParam.getPageSize());
        params.put("orderBy", queryParam.getOrderBy());
        params.put("orderDirection", queryParam.getOrderDirection());
        
        // 查询列表和总数
        List<Task> list = taskGateway.queryTasks(params);
        Long total = taskGateway.countTasks(params);
        
        return PageResult.of(list, total, queryParam.getPageNum(), queryParam.getPageSize());
    }
    
    /**
     * 完成任务
     */
    public void completeTask(CompleteTaskCmd cmd) {
        log.info("完成任务: taskId={}, assignee={}", cmd.getTaskId(), cmd.getAssignee());
        
        // 参数校验
        if (cmd.getTaskId() == null || cmd.getTaskId().trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (cmd.getAssignee() == null || cmd.getAssignee().trim().isEmpty()) {
            throw new IllegalArgumentException("办理人不能为空");
        }
        
        // 添加办理意见到变量中
        Map<String, Object> variables = cmd.getVariables();
        if (variables == null) {
            variables = new HashMap<>();
        }
        if (cmd.getComment() != null && !cmd.getComment().isEmpty()) {
            variables.put("comment", cmd.getComment());
        }
        
        taskGateway.completeTask(cmd.getTaskId(), variables, cmd.getAssignee());
    }
    
    /**
     * 认领任务
     */
    public void claimTask(String taskId, String userId) {
        log.info("认领任务: taskId={}, userId={}", taskId, userId);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        taskGateway.claimTask(taskId, userId);
    }
    
    /**
     * 委派任务
     */
    public void delegateTask(String taskId, String delegateUserId) {
        log.info("委派任务: taskId={}, delegateUserId={}", taskId, delegateUserId);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (delegateUserId == null || delegateUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("被委派人ID不能为空");
        }
        
        taskGateway.delegateTask(taskId, delegateUserId);
    }
    
    /**
     * 转办任务
     */
    public void transferTask(String taskId, String targetUserId) {
        log.info("转办任务: taskId={}, targetUserId={}", taskId, targetUserId);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (targetUserId == null || targetUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("目标用户ID不能为空");
        }
        
        taskGateway.transferTask(taskId, targetUserId);
    }
    
    /**
     * 加签任务
     */
    public void addSign(String taskId, List<String> addUserIds) {
        log.info("加签任务: taskId={}, addUserIds={}", taskId, addUserIds);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (addUserIds == null || addUserIds.isEmpty()) {
            throw new IllegalArgumentException("加签用户列表不能为空");
        }
        
        taskGateway.addSign(taskId, addUserIds);
    }
    
    /**
     * 退回任务
     */
    public void rejectTask(String taskId, String targetNodeId) {
        log.info("退回任务: taskId={}, targetNodeId={}", taskId, targetNodeId);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (targetNodeId == null || targetNodeId.trim().isEmpty()) {
            throw new IllegalArgumentException("目标节点ID不能为空");
        }
        
        taskGateway.rejectTask(taskId, targetNodeId);
    }
    
    /**
     * 解决任务（完成委派）
     */
    public void resolveTask(String taskId) {
        log.info("解决任务: taskId={}", taskId);
        
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        taskGateway.resolveTask(taskId);
    }
}

