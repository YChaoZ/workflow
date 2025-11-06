package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.task.entity.Task;
import com.bank.workflow.domain.task.gateway.TaskGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务网关实现（基于Flowable）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskGatewayImpl implements TaskGateway {
    
    private final TaskService taskService;
    private final HistoryService historyService;
    
    @Override
    public Task getTask(String taskId) {
        log.info("查询任务: taskId={}", taskId);
        
        try {
            org.flowable.task.api.Task flowableTask = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
            
            if (flowableTask == null) {
                log.warn("任务不存在: taskId={}", taskId);
                return null;
            }
            
            return convertToTask(flowableTask);
        } catch (Exception e) {
            log.error("查询任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("查询任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Task> queryTasks(Map<String, Object> params) {
        log.info("查询任务列表: params={}", params);
        
        try {
            String taskStatus = (String) params.get("taskStatus");
            
            if ("done".equals(taskStatus)) {
                // 查询已办任务
                return queryHistoricTasks(params);
            } else {
                // 查询待办任务
                return queryTodoTasks(params);
            }
        } catch (Exception e) {
            log.error("查询任务列表失败: error={}", e.getMessage(), e);
            throw new RuntimeException("查询任务列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long countTasks(Map<String, Object> params) {
        log.info("统计任务数量: params={}", params);
        
        try {
            String taskStatus = (String) params.get("taskStatus");
            
            if ("done".equals(taskStatus)) {
                // 统计已办任务
                HistoricTaskInstanceQuery query = buildHistoricTaskQuery(params);
                return query.count();
            } else {
                // 统计待办任务
                TaskQuery query = buildTaskQuery(params);
                return query.count();
            }
        } catch (Exception e) {
            log.error("统计任务数量失败: error={}", e.getMessage(), e);
            throw new RuntimeException("统计任务数量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void completeTask(String taskId, Map<String, Object> variables, String assignee) {
        log.info("完成任务: taskId={}, assignee={}", taskId, assignee);
        
        try {
            // 添加办理人信息
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("approver", assignee);
            
            taskService.complete(taskId, variables);
        } catch (Exception e) {
            log.error("完成任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("完成任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void claimTask(String taskId, String userId) {
        log.info("认领任务: taskId={}, userId={}", taskId, userId);
        
        try {
            taskService.claim(taskId, userId);
        } catch (Exception e) {
            log.error("认领任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("认领任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delegateTask(String taskId, String delegateUserId) {
        log.info("委派任务: taskId={}, delegateUserId={}", taskId, delegateUserId);
        
        try {
            taskService.delegateTask(taskId, delegateUserId);
        } catch (Exception e) {
            log.error("委派任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("委派任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void transferTask(String taskId, String targetUserId) {
        log.info("转办任务: taskId={}, targetUserId={}", taskId, targetUserId);
        
        try {
            taskService.setAssignee(taskId, targetUserId);
        } catch (Exception e) {
            log.error("转办任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("转办任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void rejectTask(String taskId, String targetNodeId) {
        log.info("退回任务: taskId={}, targetNodeId={}", taskId, targetNodeId);
        
        try {
            // 获取任务信息
            org.flowable.task.api.Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
            
            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }
            
            // 使用RuntimeService的moveActivityIdTo方法实现退回
            // 注意：这个方法在Flowable中可能需要特定配置才能使用
            // 这里使用更简单的方法：完成当前任务并设置变量来控制流转
            Map<String, Object> variables = new HashMap<>();
            variables.put("__reject__", true);
            variables.put("__targetNodeId__", targetNodeId);
            
            taskService.complete(taskId, variables);
            
            log.info("任务退回成功: taskId={}, targetNodeId={}", taskId, targetNodeId);
            
        } catch (Exception e) {
            log.error("退回任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("退回任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addSign(String taskId, List<String> addUserIds) {
        log.info("加签任务: taskId={}, addUserIds={}", taskId, addUserIds);
        
        try {
            // 获取当前任务
            org.flowable.task.api.Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
            
            if (task == null) {
                throw new RuntimeException("任务不存在: " + taskId);
            }
            
            // 为每个用户创建子任务（加签）
            for (String userId : addUserIds) {
                org.flowable.task.api.Task subTask = taskService.newTask();
                subTask.setName(task.getName() + " (加签)");
                subTask.setParentTaskId(taskId);
                subTask.setAssignee(userId);
                // 注意：子任务会自动继承父任务的流程实例ID，不需要显式设置
                
                taskService.saveTask(subTask);
                
                log.info("创建加签子任务成功: subTaskId={}, assignee={}", subTask.getId(), userId);
            }
            
        } catch (Exception e) {
            log.error("加签任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("加签任务失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void resolveTask(String taskId) {
        log.info("解决任务（完成委派）: taskId={}", taskId);
        
        try {
            // 解决任务，将任务返回给原委派人
            taskService.resolveTask(taskId);
            
            log.info("任务解决成功: taskId={}", taskId);
            
        } catch (Exception e) {
            log.error("解决任务失败: taskId={}, error={}", taskId, e.getMessage(), e);
            throw new RuntimeException("解决任务失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询待办任务
     */
    private List<Task> queryTodoTasks(Map<String, Object> params) {
        TaskQuery query = buildTaskQuery(params);
        
        // 分页参数
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        if (pageNum != null && pageSize != null) {
            int firstResult = (pageNum - 1) * pageSize;
            query.listPage(firstResult, pageSize);
        }
        
        // 排序
        String orderBy = (String) params.get("orderBy");
        String orderDirection = (String) params.get("orderDirection");
        if ("createTime".equals(orderBy)) {
            if ("asc".equalsIgnoreCase(orderDirection)) {
                query.orderByTaskCreateTime().asc();
            } else {
                query.orderByTaskCreateTime().desc();
            }
        } else if ("priority".equals(orderBy)) {
            if ("asc".equalsIgnoreCase(orderDirection)) {
                query.orderByTaskPriority().asc();
            } else {
                query.orderByTaskPriority().desc();
            }
        }
        
        List<org.flowable.task.api.Task> flowableTasks = query.list();
        
        // 转换为领域对象
        List<Task> result = new ArrayList<>();
        for (org.flowable.task.api.Task flowableTask : flowableTasks) {
            result.add(convertToTask(flowableTask));
        }
        
        return result;
    }
    
    /**
     * 查询已办任务
     */
    private List<Task> queryHistoricTasks(Map<String, Object> params) {
        HistoricTaskInstanceQuery query = buildHistoricTaskQuery(params);
        
        // 分页参数
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        if (pageNum != null && pageSize != null) {
            int firstResult = (pageNum - 1) * pageSize;
            query.listPage(firstResult, pageSize);
        }
        
        // 排序
        String orderBy = (String) params.get("orderBy");
        String orderDirection = (String) params.get("orderDirection");
        if ("createTime".equals(orderBy)) {
            if ("asc".equalsIgnoreCase(orderDirection)) {
                query.orderByHistoricTaskInstanceStartTime().asc();
            } else {
                query.orderByHistoricTaskInstanceStartTime().desc();
            }
        }
        
        List<org.flowable.task.api.history.HistoricTaskInstance> historicTasks = query.list();
        
        // 转换为领域对象
        List<Task> result = new ArrayList<>();
        for (org.flowable.task.api.history.HistoricTaskInstance historicTask : historicTasks) {
            result.add(convertToHistoricTask(historicTask));
        }
        
        return result;
    }
    
    /**
     * 构建任务查询对象
     */
    private TaskQuery buildTaskQuery(Map<String, Object> params) {
        TaskQuery query = taskService.createTaskQuery();
        
        // 任务办理人
        String assignee = (String) params.get("assignee");
        if (assignee != null && !assignee.isEmpty()) {
            query.taskAssignee(assignee);
        }
        
        // 候选用户
        String candidateUser = (String) params.get("candidateUser");
        if (candidateUser != null && !candidateUser.isEmpty()) {
            query.taskCandidateUser(candidateUser);
        }
        
        // 候选组
        String candidateGroup = (String) params.get("candidateGroup");
        if (candidateGroup != null && !candidateGroup.isEmpty()) {
            query.taskCandidateGroup(candidateGroup);
        }
        
        // 流程定义KEY
        String processKey = (String) params.get("processKey");
        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        
        // 流程实例ID
        String processInstanceId = (String) params.get("processInstanceId");
        if (processInstanceId != null && !processInstanceId.isEmpty()) {
            query.processInstanceId(processInstanceId);
        }
        
        // 业务KEY
        String businessKey = (String) params.get("businessKey");
        if (businessKey != null && !businessKey.isEmpty()) {
            query.processInstanceBusinessKey(businessKey);
        }
        
        // 任务名称
        String taskName = (String) params.get("taskName");
        if (taskName != null && !taskName.isEmpty()) {
            query.taskNameLike("%" + taskName + "%");
        }
        
        return query;
    }
    
    /**
     * 构建历史任务查询对象
     */
    private HistoricTaskInstanceQuery buildHistoricTaskQuery(Map<String, Object> params) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        
        // 只查询已完成的任务
        query.finished();
        
        // 任务办理人
        String assignee = (String) params.get("assignee");
        if (assignee != null && !assignee.isEmpty()) {
            query.taskAssignee(assignee);
        }
        
        // 流程定义KEY
        String processKey = (String) params.get("processKey");
        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        
        // 流程实例ID
        String processInstanceId = (String) params.get("processInstanceId");
        if (processInstanceId != null && !processInstanceId.isEmpty()) {
            query.processInstanceId(processInstanceId);
        }
        
        // 业务KEY
        String businessKey = (String) params.get("businessKey");
        if (businessKey != null && !businessKey.isEmpty()) {
            query.processInstanceBusinessKey(businessKey);
        }
        
        // 任务名称
        String taskName = (String) params.get("taskName");
        if (taskName != null && !taskName.isEmpty()) {
            query.taskNameLike("%" + taskName + "%");
        }
        
        return query;
    }
    
    /**
     * 转换Flowable任务为领域对象
     */
    private Task convertToTask(org.flowable.task.api.Task flowableTask) {
        Task task = new Task();
        task.setTaskId(flowableTask.getId());
        task.setTaskName(flowableTask.getName());
        task.setTaskKey(flowableTask.getTaskDefinitionKey());
        task.setProcessInstanceId(flowableTask.getProcessInstanceId());
        task.setProcessDefinitionId(flowableTask.getProcessDefinitionId());
        task.setAssignee(flowableTask.getAssignee());
        task.setOwner(flowableTask.getOwner());
        task.setCreateTime(flowableTask.getCreateTime());
        task.setStatus("TODO");
        task.setPriority(flowableTask.getPriority());
        task.setDescription(flowableTask.getDescription());
        task.setFormKey(flowableTask.getFormKey());
        
        // 获取任务变量
        Map<String, Object> variables = taskService.getVariables(flowableTask.getId());
        task.setVariables(variables != null ? variables : new HashMap<>());
        
        return task;
    }
    
    /**
     * 转换历史任务为领域对象
     */
    private Task convertToHistoricTask(org.flowable.task.api.history.HistoricTaskInstance historicTask) {
        Task task = new Task();
        task.setTaskId(historicTask.getId());
        task.setTaskName(historicTask.getName());
        task.setTaskKey(historicTask.getTaskDefinitionKey());
        task.setProcessInstanceId(historicTask.getProcessInstanceId());
        task.setProcessDefinitionId(historicTask.getProcessDefinitionId());
        task.setAssignee(historicTask.getAssignee());
        task.setOwner(historicTask.getOwner());
        task.setCreateTime(historicTask.getStartTime());
        task.setEndTime(historicTask.getEndTime());
        task.setStatus("DONE");
        task.setPriority(historicTask.getPriority());
        task.setDescription(historicTask.getDescription());
        task.setFormKey(historicTask.getFormKey());
        
        return task;
    }
}

