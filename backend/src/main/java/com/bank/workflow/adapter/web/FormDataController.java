package com.bank.workflow.adapter.web;

import com.bank.workflow.app.form.FormDataAppService;
import com.bank.workflow.app.form.command.SaveFormDataCmd;
import com.bank.workflow.domain.form.entity.FormData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单数据控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/form-data")
@RequiredArgsConstructor
public class FormDataController {

    private final FormDataAppService formDataAppService;

    /**
     * 保存表单数据
     */
    @PostMapping
    public Map<String, Object> saveFormData(@RequestBody SaveFormDataCmd cmd) {
        log.info("收到保存表单数据请求: cmd={}", cmd);
        
        Long dataId = formDataAppService.saveFormData(cmd);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "保存成功");
        response.put("data", dataId);
        return response;
    }

    /**
     * 根据ID查询表单数据
     */
    @GetMapping("/{id}")
    public Map<String, Object> getFormDataById(@PathVariable Long id) {
        log.info("收到查询表单数据请求: id={}", id);
        
        FormData formData = formDataAppService.getFormDataById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", formData);
        return response;
    }

    /**
     * 根据流程实例ID查询表单数据
     */
    @GetMapping("/process-instance/{processInstanceId}")
    public Map<String, Object> getFormDataByProcessInstanceId(@PathVariable String processInstanceId) {
        log.info("收到根据流程实例ID查询表单数据请求: processInstanceId={}", processInstanceId);
        
        FormData formData = formDataAppService.getFormDataByProcessInstanceId(processInstanceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", formData);
        return response;
    }

    /**
     * 根据任务ID查询表单数据
     */
    @GetMapping("/task/{taskId}")
    public Map<String, Object> getFormDataByTaskId(@PathVariable String taskId) {
        log.info("收到根据任务ID查询表单数据请求: taskId={}", taskId);
        
        FormData formData = formDataAppService.getFormDataByTaskId(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", formData);
        return response;
    }

    /**
     * 根据表单ID查询所有数据
     */
    @GetMapping("/form/{formId}")
    public Map<String, Object> listFormDataByFormId(@PathVariable Long formId) {
        log.info("收到根据表单ID查询所有数据请求: formId={}", formId);
        
        List<FormData> list = formDataAppService.listFormDataByFormId(formId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 删除表单数据
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteFormData(@PathVariable Long id) {
        log.info("收到删除表单数据请求: id={}", id);
        
        formDataAppService.deleteFormData(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }
}

