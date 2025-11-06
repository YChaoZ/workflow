package com.bank.workflow.domain.form.gateway;

import com.bank.workflow.domain.form.entity.FormDefinition;

import java.util.List;

/**
 * 表单定义网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface FormDefinitionGateway {
    
    /**
     * 创建表单定义
     *
     * @param formDefinition 表单定义
     * @return 表单ID
     */
    Long createFormDefinition(FormDefinition formDefinition);
    
    /**
     * 更新表单定义
     *
     * @param formDefinition 表单定义
     */
    void updateFormDefinition(FormDefinition formDefinition);
    
    /**
     * 删除表单定义（逻辑删除）
     *
     * @param formId 表单ID
     */
    void deleteFormDefinition(Long formId);
    
    /**
     * 根据ID查询表单定义
     *
     * @param formId 表单ID
     * @return 表单定义
     */
    FormDefinition getFormDefinitionById(Long formId);
    
    /**
     * 根据FormKey查询表单定义（最新版本）
     *
     * @param formKey 表单Key
     * @return 表单定义
     */
    FormDefinition getFormDefinitionByKey(String formKey);
    
    /**
     * 根据FormKey和版本查询表单定义
     *
     * @param formKey 表单Key
     * @param version 版本号
     * @return 表单定义
     */
    FormDefinition getFormDefinitionByKeyAndVersion(String formKey, Integer version);
    
    /**
     * 查询所有表单定义
     *
     * @return 表单定义列表
     */
    List<FormDefinition> listAllFormDefinitions();
    
    /**
     * 根据分类查询表单定义
     *
     * @param categoryId 分类ID
     * @return 表单定义列表
     */
    List<FormDefinition> listFormDefinitionsByCategory(Long categoryId);
    
    /**
     * 根据状态查询表单定义
     *
     * @param status 状态
     * @return 表单定义列表
     */
    List<FormDefinition> listFormDefinitionsByStatus(Integer status);
    
    /**
     * 查询表单的所有版本
     *
     * @param formKey 表单Key
     * @return 表单定义列表
     */
    List<FormDefinition> listFormVersions(String formKey);
    
    /**
     * 发布表单（创建新版本）
     *
     * @param formKey 表单Key
     * @return 新版本号
     */
    Integer publishFormVersion(String formKey);
}

