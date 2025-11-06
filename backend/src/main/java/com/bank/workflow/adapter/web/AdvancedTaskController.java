package com.bank.workflow.adapter.web;

import com.bank.workflow.app.task.AdvancedTaskAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高级任务操作控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks/advanced")
@RequiredArgsConstructor
public class AdvancedTaskController {

    private final AdvancedTaskAppService advancedTaskAppService;

    /**
     * 任务加签
     *
     * @param taskId 任务ID
     * @param request 加签请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/add-sign")
    public Map<String, Object> addSign(
            @PathVariable String taskId,
            @RequestBody AddSignRequest request) {

        log.info("任务加签请求，任务ID: {}, 加签人: {}, 类型: {}", 
                taskId, request.getAssignees(), request.getType());

        boolean success = advancedTaskAppService.addSign(
                taskId, 
                request.getAssignees(), 
                request.getType());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "加签成功" : "加签失败");
        result.put("data", success);
        return result;
    }

    /**
     * 任务转办
     *
     * @param taskId 任务ID
     * @param request 转办请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/transfer")
    public Map<String, Object> transfer(
            @PathVariable String taskId,
            @RequestBody TransferRequest request) {

        log.info("任务转办请求，任务ID: {}, 目标用户: {}", taskId, request.getTargetUser());

        boolean success = advancedTaskAppService.transfer(
                taskId, 
                request.getTargetUser(), 
                request.getComment());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "转办成功" : "转办失败");
        result.put("data", success);
        return result;
    }

    /**
     * 任务委派
     *
     * @param taskId 任务ID
     * @param request 委派请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/delegate")
    public Map<String, Object> delegate(
            @PathVariable String taskId,
            @RequestBody DelegateRequest request) {

        log.info("任务委派请求，任务ID: {}, 目标用户: {}", taskId, request.getTargetUser());

        boolean success = advancedTaskAppService.delegate(taskId, request.getTargetUser());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "委派成功" : "委派失败");
        result.put("data", success);
        return result;
    }

    /**
     * 任务回退到上一节点
     *
     * @param taskId 任务ID
     * @param request 回退请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/reject/previous")
    public Map<String, Object> rejectToPrevious(
            @PathVariable String taskId,
            @RequestBody RejectRequest request) {

        log.info("任务回退到上一节点，任务ID: {}", taskId);

        boolean success = advancedTaskAppService.rejectToPrevious(taskId, request.getComment());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "回退成功" : "回退失败");
        result.put("data", success);
        return result;
    }

    /**
     * 任务回退到指定节点
     *
     * @param taskId 任务ID
     * @param request 回退请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/reject/node")
    public Map<String, Object> rejectToNode(
            @PathVariable String taskId,
            @RequestBody RejectToNodeRequest request) {

        log.info("任务回退到指定节点，任务ID: {}, 目标节点: {}", 
                taskId, request.getTargetNodeId());

        boolean success = advancedTaskAppService.rejectToNode(
                taskId, 
                request.getTargetNodeId(), 
                request.getComment());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "回退成功" : "回退失败");
        result.put("data", success);
        return result;
    }

    /**
     * 任务回退到流程发起人
     *
     * @param taskId 任务ID
     * @param request 回退请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/reject/start")
    public Map<String, Object> rejectToStart(
            @PathVariable String taskId,
            @RequestBody RejectRequest request) {

        log.info("任务回退到流程发起人，任务ID: {}", taskId);

        boolean success = advancedTaskAppService.rejectToStart(taskId, request.getComment());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "回退成功" : "回退失败");
        result.put("data", success);
        return result;
    }

    /**
     * 撤回流程
     *
     * @param processInstanceId 流程实例ID
     * @param request 撤回请求
     * @return 操作结果
     */
    @PostMapping("/process/{processInstanceId}/withdraw")
    public Map<String, Object> withdrawProcess(
            @PathVariable String processInstanceId,
            @RequestBody WithdrawRequest request) {

        log.info("撤回流程，流程实例ID: {}", processInstanceId);

        boolean success = advancedTaskAppService.withdrawProcess(
                processInstanceId, 
                request.getReason());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "撤回成功" : "撤回失败");
        result.put("data", success);
        return result;
    }

    /**
     * 撤回任务
     *
     * @param taskId 任务ID
     * @param request 撤回请求
     * @return 操作结果
     */
    @PostMapping("/{taskId}/withdraw")
    public Map<String, Object> withdrawTask(
            @PathVariable String taskId,
            @RequestBody WithdrawRequest request) {

        log.info("撤回任务，任务ID: {}", taskId);

        boolean success = advancedTaskAppService.withdrawTask(taskId, request.getReason());

        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "撤回成功" : "撤回失败");
        result.put("data", success);
        return result;
    }

    /**
     * 获取可回退的节点列表
     *
     * @param taskId 任务ID
     * @return 可回退节点列表
     */
    @GetMapping("/{taskId}/rejectable-nodes")
    public Map<String, Object> getRejectableNodes(@PathVariable String taskId) {
        log.info("获取可回退节点，任务ID: {}", taskId);

        List<Map<String, Object>> nodes = advancedTaskAppService.getRejectableNodes(taskId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", nodes);
        return result;
    }

    /**
     * 获取历史审批人
     *
     * @param taskId 任务ID
     * @return 历史审批人列表
     */
    @GetMapping("/{taskId}/historic-assignees")
    public Map<String, Object> getHistoricAssignees(@PathVariable String taskId) {
        log.info("获取历史审批人，任务ID: {}", taskId);

        List<String> assignees = advancedTaskAppService.getHistoricAssignees(taskId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", assignees);
        return result;
    }

    // ========== 内部类：请求DTO ==========

    /**
     * 加签请求
     */
    public static class AddSignRequest {
        private List<String> assignees;
        private String type; // AND-会签, OR-或签

        public List<String> getAssignees() {
            return assignees;
        }

        public void setAssignees(List<String> assignees) {
            this.assignees = assignees;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 转办请求
     */
    public static class TransferRequest {
        private String targetUser;
        private String comment;

        public String getTargetUser() {
            return targetUser;
        }

        public void setTargetUser(String targetUser) {
            this.targetUser = targetUser;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /**
     * 委派请求
     */
    public static class DelegateRequest {
        private String targetUser;

        public String getTargetUser() {
            return targetUser;
        }

        public void setTargetUser(String targetUser) {
            this.targetUser = targetUser;
        }
    }

    /**
     * 回退请求
     */
    public static class RejectRequest {
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /**
     * 回退到指定节点请求
     */
    public static class RejectToNodeRequest {
        private String targetNodeId;
        private String comment;

        public String getTargetNodeId() {
            return targetNodeId;
        }

        public void setTargetNodeId(String targetNodeId) {
            this.targetNodeId = targetNodeId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /**
     * 撤回请求
     */
    public static class WithdrawRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}

