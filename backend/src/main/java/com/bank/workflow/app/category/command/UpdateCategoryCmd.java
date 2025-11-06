package com.bank.workflow.app.category.command;

import lombok.Data;

/**
 * 更新流程分类命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UpdateCategoryCmd {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 描述
     */
    private String description;
}

