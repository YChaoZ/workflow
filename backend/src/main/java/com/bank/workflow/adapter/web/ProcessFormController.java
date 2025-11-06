package com.bank.workflow.adapter.web;

import com.bank.workflow.app.form.ProcessFormAppService;
import com.bank.workflow.domain.form.entity.ProcessForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程表单关联控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/process-forms")
@RequiredArgsConstructor
public class ProcessFormController {

    private final ProcessFormAppService processFormAppService;

    /**
     * 绑定表单到流程
     */
    @PostMapping
    public Map<String, Object> bindFormToProcess(@RequestBody ProcessForm processForm) {
        log.info("收到绑定表单到流程请求: processForm={}", processForm);
        
        Long id = processFormAppService.bindFormToProcess(processForm);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "绑定成功");
        response.put("data", id);
        return response;
    }

    /**
     * 更新流程表单关联
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateProcessForm(@PathVariable Long id, 
                                                  @RequestBody ProcessForm processForm) {
        log.info("收到更新流程表单关联请求: id={}, processForm={}", id, processForm);
        
        processForm.setId(id);
        processFormAppService.updateProcessForm(processForm);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return response;
    }

    /**
     * 解绑表单
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> unbindForm(@PathVariable Long id) {
        log.info("收到解绑表单请求: id={}", id);
        
        processFormAppService.unbindForm(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "解绑成功");
        return response;
    }

    /**
     * 根据ID查询流程表单关联
     */
    @GetMapping("/{id}")
    public Map<String, Object> getProcessFormById(@PathVariable Long id) {
        log.info("收到查询流程表单关联请求: id={}", id);
        
        ProcessForm processForm = processFormAppService.getProcessFormById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", processForm);
        return response;
    }

    /**
     * 根据流程定义Key查询所有表单关联
     */
    @GetMapping("/process/{processDefinitionKey}")
    public Map<String, Object> listProcessFormsByProcessKey(@PathVariable String processDefinitionKey) {
        log.info("收到根据流程定义Key查询所有表单关联请求: processKey={}", processDefinitionKey);
        
        List<ProcessForm> list = processFormAppService.listProcessFormsByProcessKey(processDefinitionKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 根据流程定义Key和节点ID查询表单关联
     */
    @GetMapping("/process/{processDefinitionKey}/node/{nodeId}")
    public Map<String, Object> getProcessFormByProcessKeyAndNodeId(
            @PathVariable String processDefinitionKey,
            @PathVariable String nodeId) {
        log.info("收到根据流程定义Key和节点ID查询表单关联请求: processKey={}, nodeId={}", 
                processDefinitionKey, nodeId);
        
        ProcessForm processForm = processFormAppService.getProcessFormByProcessKeyAndNodeId(
                processDefinitionKey, nodeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", processForm);
        return response;
    }

    /**
     * 查询流程的全局表单
     */
    @GetMapping("/process/{processDefinitionKey}/global")
    public Map<String, Object> getGlobalFormByProcessKey(@PathVariable String processDefinitionKey) {
        log.info("收到查询流程全局表单请求: processKey={}", processDefinitionKey);
        
        ProcessForm processForm = processFormAppService.getGlobalFormByProcessKey(processDefinitionKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", processForm);
        return response;
    }
}

