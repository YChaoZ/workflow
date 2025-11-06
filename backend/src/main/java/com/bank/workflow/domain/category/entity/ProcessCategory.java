package com.bank.workflow.domain.category.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程分类领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessCategory {
    
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
     * 父分类ID（0表示根节点）
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
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
    
    /**
     * 子分类列表（用于构建树形结构）
     */
    private List<ProcessCategory> children = new ArrayList<>();
}

