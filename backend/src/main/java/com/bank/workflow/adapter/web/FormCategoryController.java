package com.bank.workflow.adapter.web;

import com.bank.workflow.app.form.FormCategoryAppService;
import com.bank.workflow.domain.form.entity.FormCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单分类控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/form-categories")
@RequiredArgsConstructor
public class FormCategoryController {

    private final FormCategoryAppService formCategoryAppService;

    /**
     * 创建表单分类
     */
    @PostMapping
    public Map<String, Object> createFormCategory(@RequestBody FormCategory category) {
        log.info("收到创建表单分类请求: category={}", category);
        
        Long categoryId = formCategoryAppService.createFormCategory(category);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", categoryId);
        return response;
    }

    /**
     * 更新表单分类
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateFormCategory(@PathVariable Long id, 
                                                   @RequestBody FormCategory category) {
        log.info("收到更新表单分类请求: id={}, category={}", id, category);
        
        category.setId(id);
        formCategoryAppService.updateFormCategory(category);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return response;
    }

    /**
     * 删除表单分类
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteFormCategory(@PathVariable Long id) {
        log.info("收到删除表单分类请求: id={}", id);
        
        formCategoryAppService.deleteFormCategory(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    /**
     * 根据ID查询表单分类
     */
    @GetMapping("/{id}")
    public Map<String, Object> getFormCategoryById(@PathVariable Long id) {
        log.info("收到查询表单分类请求: id={}", id);
        
        FormCategory category = formCategoryAppService.getFormCategoryById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", category);
        return response;
    }

    /**
     * 查询表单分类树
     */
    @GetMapping("/tree")
    public Map<String, Object> getFormCategoryTree() {
        log.info("收到查询表单分类树请求");
        
        List<FormCategory> tree = formCategoryAppService.getFormCategoryTree();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", tree);
        return response;
    }

    /**
     * 查询所有表单分类（列表）
     */
    @GetMapping("/list")
    public Map<String, Object> listAllFormCategories() {
        log.info("收到查询所有表单分类请求");
        
        List<FormCategory> list = formCategoryAppService.listAllFormCategories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", list);
        return response;
    }
}

