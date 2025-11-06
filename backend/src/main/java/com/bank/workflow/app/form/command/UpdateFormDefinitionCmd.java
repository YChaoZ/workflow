package com.bank.workflow.app.form.command;

import lombok.Data;

/**
 * 更新表单定义命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UpdateFormDefinitionCmd {
    
    /**
     * 表单ID
     */
    private Long id;
    
    /**
     * 表单名称
     */
    private String formName;
    
    /**
     * 表单类型
     */
    private String formType;
    
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
     * 状态
     */
    private Integer status;
    
    /**
     * 更新人
     */
    private String updateBy;
}

