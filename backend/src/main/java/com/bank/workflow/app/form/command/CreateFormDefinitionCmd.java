package com.bank.workflow.app.form.command;

import lombok.Data;

/**
 * 创建表单定义命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateFormDefinitionCmd {
    
    /**
     * 表单Key（唯一标识）
     */
    private String formKey;
    
    /**
     * 表单名称
     */
    private String formName;
    
    /**
     * 表单类型（CUSTOM:自定义, BUILT_IN:内置）
     */
    private String formType = "CUSTOM";
    
    /**
     * 表单配置JSON
     */
    private String formConfig;
    
    /**
     * 表单分类ID
     */
    private Long categoryId;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态（0:禁用 1:启用 2:草稿）
     */
    private Integer status = 2; // 默认为草稿
    
    /**
     * 创建人
     */
    private String createBy;
}

