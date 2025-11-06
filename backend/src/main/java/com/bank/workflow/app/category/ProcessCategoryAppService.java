package com.bank.workflow.app.category;

import com.bank.workflow.app.category.command.CreateCategoryCmd;
import com.bank.workflow.app.category.command.UpdateCategoryCmd;
import com.bank.workflow.domain.category.entity.ProcessCategory;
import com.bank.workflow.domain.category.gateway.ProcessCategoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程分类应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessCategoryAppService {
    
    private final ProcessCategoryGateway processCategoryGateway;
    
    /**
     * 创建流程分类
     */
    public Long createCategory(CreateCategoryCmd cmd) {
        log.info("创建流程分类: cmd={}", cmd);
        
        // 参数校验
        if (cmd.getName() == null || cmd.getName().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (cmd.getCode() == null || cmd.getCode().isEmpty()) {
            throw new IllegalArgumentException("分类编码不能为空");
        }
        
        // 检查编码是否已存在
        ProcessCategory existingCategory = processCategoryGateway.getCategoryByCode(cmd.getCode());
        if (existingCategory != null) {
            throw new IllegalArgumentException("分类编码已存在: " + cmd.getCode());
        }
        
        // 转换为领域实体
        ProcessCategory category = new ProcessCategory();
        BeanUtils.copyProperties(cmd, category);
        
        // 创建分类
        return processCategoryGateway.createCategory(category);
    }
    
    /**
     * 更新流程分类
     */
    public void updateCategory(UpdateCategoryCmd cmd) {
        log.info("更新流程分类: cmd={}", cmd);
        
        // 参数校验
        if (cmd.getId() == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        if (cmd.getName() == null || cmd.getName().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (cmd.getCode() == null || cmd.getCode().isEmpty()) {
            throw new IllegalArgumentException("分类编码不能为空");
        }
        
        // 检查分类是否存在
        ProcessCategory existingCategory = processCategoryGateway.getCategoryById(cmd.getId());
        if (existingCategory == null) {
            throw new IllegalArgumentException("分类不存在: " + cmd.getId());
        }
        
        // 检查编码是否被其他分类使用
        ProcessCategory categoryWithSameCode = processCategoryGateway.getCategoryByCode(cmd.getCode());
        if (categoryWithSameCode != null && !categoryWithSameCode.getId().equals(cmd.getId())) {
            throw new IllegalArgumentException("分类编码已被使用: " + cmd.getCode());
        }
        
        // 不能将分类移动到自己的子分类下（防止循环引用）
        if (cmd.getParentId() != null && cmd.getParentId().equals(cmd.getId())) {
            throw new IllegalArgumentException("不能将分类移动到自己下");
        }
        
        // 转换为领域实体
        ProcessCategory category = new ProcessCategory();
        BeanUtils.copyProperties(cmd, category);
        
        // 更新分类
        processCategoryGateway.updateCategory(category);
    }
    
    /**
     * 删除流程分类
     */
    public void deleteCategory(Long categoryId) {
        log.info("删除流程分类: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        // 检查分类是否存在
        ProcessCategory category = processCategoryGateway.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在: " + categoryId);
        }
        
        // 检查是否有子分类
        Long childrenCount = processCategoryGateway.countChildrenCategories(categoryId);
        if (childrenCount > 0) {
            throw new IllegalArgumentException("该分类下还有子分类，不能删除");
        }
        
        // 删除分类
        processCategoryGateway.deleteCategory(categoryId);
    }
    
    /**
     * 查询流程分类详情
     */
    public ProcessCategory getCategoryById(Long categoryId) {
        log.info("查询流程分类详情: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        return processCategoryGateway.getCategoryById(categoryId);
    }
    
    /**
     * 查询所有流程分类（平铺列表）
     */
    public List<ProcessCategory> listAllCategories() {
        log.info("查询所有流程分类");
        
        return processCategoryGateway.listAllCategories();
    }
    
    /**
     * 查询流程分类树（树形结构）
     */
    public List<ProcessCategory> getCategoryTree() {
        log.info("查询流程分类树");
        
        // 查询所有分类
        List<ProcessCategory> allCategories = processCategoryGateway.listAllCategories();
        
        // 构建树形结构
        return buildCategoryTree(allCategories, 0L);
    }
    
    /**
     * 查询指定父分类下的子分类
     */
    public List<ProcessCategory> listChildrenCategories(Long parentId) {
        log.info("查询子分类: parentId={}", parentId);
        
        if (parentId == null) {
            parentId = 0L;
        }
        
        return processCategoryGateway.listChildrenCategories(parentId);
    }
    
    /**
     * 构建分类树形结构
     */
    private List<ProcessCategory> buildCategoryTree(List<ProcessCategory> allCategories, Long parentId) {
        List<ProcessCategory> result = new ArrayList<>();
        
        // 创建ID到分类的映射
        Map<Long, ProcessCategory> categoryMap = new HashMap<>();
        for (ProcessCategory category : allCategories) {
            categoryMap.put(category.getId(), category);
            category.setChildren(new ArrayList<>()); // 初始化children列表
        }
        
        // 构建树形结构
        for (ProcessCategory category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                // 根节点
                result.add(category);
            } else {
                // 非根节点，添加到父节点的children中
                ProcessCategory parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }
        
        return result;
    }
}

