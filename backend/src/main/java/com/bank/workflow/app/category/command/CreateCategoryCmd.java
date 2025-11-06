package com.bank.workflow.app.category.command;

import lombok.Data;

/**
 * 创建流程分类命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateCategoryCmd {
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 父分类ID（0表示根节点）
     */
    private Long parentId = 0L;
    
    /**
     * 排序
     */
    private Integer sortOrder = 0;
    
    /**
     * 描述
     */
    private String description;
}

