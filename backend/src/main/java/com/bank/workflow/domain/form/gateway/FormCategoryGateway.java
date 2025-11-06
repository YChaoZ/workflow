package com.bank.workflow.domain.form.gateway;

import com.bank.workflow.domain.form.entity.FormCategory;

import java.util.List;

/**
 * 表单分类网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface FormCategoryGateway {
    
    /**
     * 创建表单分类
     *
     * @param category 表单分类
     * @return 分类ID
     */
    Long createFormCategory(FormCategory category);
    
    /**
     * 更新表单分类
     *
     * @param category 表单分类
     */
    void updateFormCategory(FormCategory category);
    
    /**
     * 删除表单分类
     *
     * @param categoryId 分类ID
     */
    void deleteFormCategory(Long categoryId);
    
    /**
     * 根据ID查询表单分类
     *
     * @param categoryId 分类ID
     * @return 表单分类
     */
    FormCategory getFormCategoryById(Long categoryId);
    
    /**
     * 查询所有表单分类
     *
     * @return 表单分类列表
     */
    List<FormCategory> listAllFormCategories();
    
    /**
     * 根据父分类ID查询子分类
     *
     * @param parentId 父分类ID
     * @return 表单分类列表
     */
    List<FormCategory> listFormCategoriesByParentId(Long parentId);
    
    /**
     * 统计子分类数量
     *
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    long countChildrenCategories(Long categoryId);
}

