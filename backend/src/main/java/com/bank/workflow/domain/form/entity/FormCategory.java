package com.bank.workflow.domain.form.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单分类实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class FormCategory {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类编码
     */
    private String categoryCode;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
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
    
    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子分类列表（非持久化字段）
     */
    private List<FormCategory> children;
    
    /**
     * 是否为根分类
     */
    public boolean isRoot() {
        return parentId == null || parentId == 0;
    }
}

