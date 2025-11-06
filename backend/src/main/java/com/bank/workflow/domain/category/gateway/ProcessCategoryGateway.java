package com.bank.workflow.domain.category.gateway;

import com.bank.workflow.domain.category.entity.ProcessCategory;

import java.util.List;

/**
 * 流程分类网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface ProcessCategoryGateway {
    
    /**
     * 创建流程分类
     *
     * @param category 流程分类
     * @return 分类ID
     */
    Long createCategory(ProcessCategory category);
    
    /**
     * 更新流程分类
     *
     * @param category 流程分类
     */
    void updateCategory(ProcessCategory category);
    
    /**
     * 删除流程分类
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
    
    /**
     * 查询流程分类详情
     *
     * @param categoryId 分类ID
     * @return 流程分类
     */
    ProcessCategory getCategoryById(Long categoryId);
    
    /**
     * 根据编码查询流程分类
     *
     * @param code 分类编码
     * @return 流程分类
     */
    ProcessCategory getCategoryByCode(String code);
    
    /**
     * 查询所有流程分类（平铺列表）
     *
     * @return 流程分类列表
     */
    List<ProcessCategory> listAllCategories();
    
    /**
     * 查询指定父分类下的子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<ProcessCategory> listChildrenCategories(Long parentId);
    
    /**
     * 统计指定父分类下的子分类数量
     *
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    Long countChildrenCategories(Long parentId);
}

