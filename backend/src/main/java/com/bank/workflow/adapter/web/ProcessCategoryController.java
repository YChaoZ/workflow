package com.bank.workflow.adapter.web;

import com.bank.workflow.app.category.ProcessCategoryAppService;
import com.bank.workflow.app.category.command.CreateCategoryCmd;
import com.bank.workflow.app.category.command.UpdateCategoryCmd;
import com.bank.workflow.domain.category.entity.ProcessCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程分类控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/process/category")
@RequiredArgsConstructor
public class ProcessCategoryController {
    
    private final ProcessCategoryAppService processCategoryAppService;
    
    /**
     * 创建流程分类
     */
    @PostMapping
    public Map<String, Object> createCategory(@RequestBody CreateCategoryCmd cmd) {
        log.info("接收创建流程分类请求: cmd={}", cmd);
        
        Long categoryId = processCategoryAppService.createCategory(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", Map.of("categoryId", categoryId));
        return result;
    }
    
    /**
     * 更新流程分类
     */
    @PutMapping("/{categoryId}")
    public Map<String, Object> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody UpdateCategoryCmd cmd) {
        log.info("接收更新流程分类请求: categoryId={}, cmd={}", categoryId, cmd);
        
        cmd.setId(categoryId); // 设置ID
        processCategoryAppService.updateCategory(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return result;
    }
    
    /**
     * 删除流程分类
     */
    @DeleteMapping("/{categoryId}")
    public Map<String, Object> deleteCategory(@PathVariable Long categoryId) {
        log.info("接收删除流程分类请求: categoryId={}", categoryId);
        
        processCategoryAppService.deleteCategory(categoryId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 查询流程分类详情
     */
    @GetMapping("/{categoryId}")
    public Map<String, Object> getCategoryById(@PathVariable Long categoryId) {
        log.info("接收查询流程分类详情请求: categoryId={}", categoryId);
        
        ProcessCategory category = processCategoryAppService.getCategoryById(categoryId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", category);
        return result;
    }
    
    /**
     * 查询所有流程分类（平铺列表）
     */
    @GetMapping("/list")
    public Map<String, Object> listAllCategories() {
        log.info("接收查询所有流程分类请求");
        
        List<ProcessCategory> categories = processCategoryAppService.listAllCategories();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", categories);
        return result;
    }
    
    /**
     * 查询流程分类树（树形结构）
     */
    @GetMapping("/tree")
    public Map<String, Object> getCategoryTree() {
        log.info("接收查询流程分类树请求");
        
        List<ProcessCategory> categoryTree = processCategoryAppService.getCategoryTree();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", categoryTree);
        return result;
    }
    
    /**
     * 查询指定父分类下的子分类
     */
    @GetMapping("/children/{parentId}")
    public Map<String, Object> listChildrenCategories(@PathVariable Long parentId) {
        log.info("接收查询子分类请求: parentId={}", parentId);
        
        List<ProcessCategory> children = processCategoryAppService.listChildrenCategories(parentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", children);
        return result;
    }
}

