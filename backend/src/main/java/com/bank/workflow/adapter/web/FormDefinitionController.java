package com.bank.workflow.adapter.web;

import com.bank.workflow.app.form.FormDefinitionAppService;
import com.bank.workflow.app.form.command.CreateFormDefinitionCmd;
import com.bank.workflow.app.form.command.UpdateFormDefinitionCmd;
import com.bank.workflow.domain.form.entity.FormDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单定义控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormDefinitionController {

    private final FormDefinitionAppService formDefinitionAppService;

    /**
     * 创建表单定义
     */
    @PostMapping
    public Map<String, Object> createFormDefinition(@RequestBody CreateFormDefinitionCmd cmd) {
        log.info("收到创建表单定义请求: cmd={}", cmd);
        
        Long formId = formDefinitionAppService.createFormDefinition(cmd);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", formId);
        return response;
    }

    /**
     * 更新表单定义
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateFormDefinition(@PathVariable Long id, 
                                                     @RequestBody UpdateFormDefinitionCmd cmd) {
        log.info("收到更新表单定义请求: id={}, cmd={}", id, cmd);
        
        cmd.setId(id);
        formDefinitionAppService.updateFormDefinition(cmd);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return response;
    }

    /**
     * 删除表单定义
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteFormDefinition(@PathVariable Long id) {
        log.info("收到删除表单定义请求: id={}", id);
        
        formDefinitionAppService.deleteFormDefinition(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    /**
     * 根据ID查询表单定义
     */
    @GetMapping("/{id}")
    public Map<String, Object> getFormDefinitionById(@PathVariable Long id) {
        log.info("收到查询表单定义请求: id={}", id);
        
        FormDefinition formDefinition = formDefinitionAppService.getFormDefinitionById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", formDefinition);
        return response;
    }

    /**
     * 根据FormKey查询表单定义（最新版本）
     */
    @GetMapping("/key/{formKey}")
    public Map<String, Object> getFormDefinitionByKey(@PathVariable String formKey) {
        log.info("收到查询表单定义请求: formKey={}", formKey);
        
        FormDefinition formDefinition = formDefinitionAppService.getFormDefinitionByKey(formKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", formDefinition);
        return response;
    }

    /**
     * 查询所有表单定义
     */
    @GetMapping("/list")
    public Map<String, Object> listAllFormDefinitions() {
        log.info("收到查询所有表单定义请求");
        
        List<FormDefinition> list = formDefinitionAppService.listAllFormDefinitions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 根据分类查询表单定义
     */
    @GetMapping("/category/{categoryId}")
    public Map<String, Object> listFormDefinitionsByCategory(@PathVariable Long categoryId) {
        log.info("收到根据分类查询表单定义请求: categoryId={}", categoryId);
        
        List<FormDefinition> list = formDefinitionAppService.listFormDefinitionsByCategory(categoryId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 根据状态查询表单定义
     */
    @GetMapping("/status/{status}")
    public Map<String, Object> listFormDefinitionsByStatus(@PathVariable Integer status) {
        log.info("收到根据状态查询表单定义请求: status={}", status);
        
        List<FormDefinition> list = formDefinitionAppService.listFormDefinitionsByStatus(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 查询表单的所有版本
     */
    @GetMapping("/versions/{formKey}")
    public Map<String, Object> listFormVersions(@PathVariable String formKey) {
        log.info("收到查询表单所有版本请求: formKey={}", formKey);
        
        List<FormDefinition> list = formDefinitionAppService.listFormVersions(formKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }

    /**
     * 发布表单（创建新版本）
     */
    @PostMapping("/publish/{formKey}")
    public Map<String, Object> publishFormVersion(@PathVariable String formKey) {
        log.info("收到发布表单请求: formKey={}", formKey);
        
        Integer newVersion = formDefinitionAppService.publishFormVersion(formKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "发布成功");
        response.put("data", newVersion);
        return response;
    }
}

