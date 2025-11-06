package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.task.gateway.AdvancedTaskGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 高级任务操作网关实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvancedTaskGatewayImpl implements AdvancedTaskGateway {

    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;

    @Override
    public boolean addSign(String taskId, List<String> assignees, String type) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            // 为每个加签人创建子任务
            for (String assignee : assignees) {
                Task subTask = taskService.newTask();
                subTask.setName(task.getName() + "-加签");
                subTask.setAssignee(assignee);
                subTask.setParentTaskId(taskId);
                
                taskService.saveTask(subTask);
                
                // 设置变量标记加签类型和流程信息
                taskService.setVariableLocal(subTask.getId(), "signType", type);
                taskService.setVariableLocal(subTask.getId(), "processInstanceId", task.getProcessInstanceId());
                taskService.setVariableLocal(subTask.getId(), "taskDefinitionKey", task.getTaskDefinitionKey());
            }

            // 设置父任务为等待状态
            taskService.setVariableLocal(taskId, "waitingForSign", true);
            taskService.setVariableLocal(taskId, "signType", type);
            taskService.setVariableLocal(taskId, "signAssignees", assignees);

            log.info("任务加签成功，任务ID: {}, 加签人: {}, 类型: {}", taskId, assignees, type);
            return true;
        } catch (Exception e) {
            log.error("任务加签失败", e);
            return false;
        }
    }

    @Override
    public boolean transfer(String taskId, String targetUser, String comment) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            String originalAssignee = task.getAssignee();

            // 添加转办评论
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), 
                        "transfer", "转办：" + comment);
            }

            // 设置转办变量
            taskService.setVariableLocal(taskId, "transferred", true);
            taskService.setVariableLocal(taskId, "originalAssignee", originalAssignee);
            taskService.setVariableLocal(taskId, "transferTime", new Date());

            // 变更任务处理人
            taskService.setAssignee(taskId, targetUser);

            log.info("任务转办成功，任务ID: {}, 原处理人: {}, 新处理人: {}", 
                    taskId, originalAssignee, targetUser);
            return true;
        } catch (Exception e) {
            log.error("任务转办失败", e);
            return false;
        }
    }

    @Override
    public boolean delegate(String taskId, String targetUser) {
        try {
            // 使用Flowable的委派功能
            taskService.delegateTask(taskId, targetUser);
            
            log.info("任务委派成功，任务ID: {}, 委派给: {}", taskId, targetUser);
            return true;
        } catch (Exception e) {
            log.error("任务委派失败", e);
            return false;
        }
    }

    @Override
    public boolean rejectToPrevious(String taskId, String comment) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            // 获取上一个节点
            List<HistoricActivityInstance> historicActivities = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .activityType("userTask")
                    .finished()
                    .orderByHistoricActivityInstanceEndTime().desc()
                    .list();

            if (historicActivities.isEmpty()) {
                log.error("找不到上一个节点");
                return false;
            }

            String targetNodeId = historicActivities.get(0).getActivityId();
            return rejectToNode(taskId, targetNodeId, comment);
        } catch (Exception e) {
            log.error("任务回退失败", e);
            return false;
        }
    }

    @Override
    public boolean rejectToNode(String taskId, String targetNodeId, String comment) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            // 添加回退评论
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), 
                        "reject", "回退：" + comment);
            }

            // 获取当前执行
            List<Execution> executions = runtimeService.createExecutionQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .activityId(task.getTaskDefinitionKey())
                    .list();

            if (executions.isEmpty()) {
                log.error("找不到当前执行");
                return false;
            }

            // 使用Flowable的回退功能
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdTo(task.getTaskDefinitionKey(), targetNodeId)
                    .changeState();

            log.info("任务回退成功，任务ID: {}, 目标节点: {}", taskId, targetNodeId);
            return true;
        } catch (Exception e) {
            log.error("任务回退到指定节点失败", e);
            return false;
        }
    }

    @Override
    public boolean rejectToStart(String taskId, String comment) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            // 获取流程的第一个用户任务
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
            
            String firstUserTaskId = null;
            for (FlowElement element : flowElements) {
                if (element instanceof UserTask) {
                    firstUserTaskId = element.getId();
                    break;
                }
            }

            if (firstUserTaskId == null) {
                log.error("找不到流程的第一个用户任务");
                return false;
            }

            return rejectToNode(taskId, firstUserTaskId, comment);
        } catch (Exception e) {
            log.error("任务回退到开始失败", e);
            return false;
        }
    }

    @Override
    public boolean withdrawProcess(String processInstanceId, String reason) {
        try {
            HistoricProcessInstance processInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance == null) {
                log.error("流程实例不存在: {}", processInstanceId);
                return false;
            }

            if (processInstance.getEndTime() != null) {
                log.error("流程已结束，不能撤回");
                return false;
            }

            // 删除流程实例
            runtimeService.deleteProcessInstance(processInstanceId, reason);

            log.info("流程撤回成功，流程实例ID: {}, 原因: {}", processInstanceId, reason);
            return true;
        } catch (Exception e) {
            log.error("流程撤回失败", e);
            return false;
        }
    }

    @Override
    public boolean withdrawTask(String taskId, String reason) {
        try {
            // 查询历史任务
            var historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (historicTask == null) {
                log.error("任务不存在: {}", taskId);
                return false;
            }

            if (historicTask.getEndTime() == null) {
                log.error("任务未完成，不能撤回");
                return false;
            }

            // 获取当前流程的活动任务
            List<Task> activeTasks = taskService.createTaskQuery()
                    .processInstanceId(historicTask.getProcessInstanceId())
                    .list();

            if (activeTasks.isEmpty()) {
                log.error("流程已无活动任务");
                return false;
            }

            // 回退到撤回的任务节点
            String currentTaskDefKey = activeTasks.get(0).getTaskDefinitionKey();
            String targetTaskDefKey = historicTask.getTaskDefinitionKey();

            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(historicTask.getProcessInstanceId())
                    .moveActivityIdTo(currentTaskDefKey, targetTaskDefKey)
                    .changeState();

            log.info("任务撤回成功，任务ID: {}, 原因: {}", taskId, reason);
            return true;
        } catch (Exception e) {
            log.error("任务撤回失败", e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getRejectableNodes(String taskId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return Collections.emptyList();
            }

            // 获取历史用户任务
            List<HistoricActivityInstance> historicActivities = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .activityType("userTask")
                    .finished()
                    .orderByHistoricActivityInstanceStartTime().asc()
                    .list();

            return historicActivities.stream()
                    .map(activity -> {
                        Map<String, Object> node = new HashMap<>();
                        node.put("activityId", activity.getActivityId());
                        node.put("activityName", activity.getActivityName());
                        node.put("assignee", activity.getAssignee());
                        node.put("startTime", activity.getStartTime());
                        node.put("endTime", activity.getEndTime());
                        return node;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取可回退节点失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getHistoricAssignees(String taskId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                log.error("任务不存在: {}", taskId);
                return Collections.emptyList();
            }

            // 获取历史审批人
            List<HistoricActivityInstance> historicActivities = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .activityType("userTask")
                    .finished()
                    .list();

            return historicActivities.stream()
                    .map(HistoricActivityInstance::getAssignee)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取历史审批人失败", e);
            return Collections.emptyList();
        }
    }
}

