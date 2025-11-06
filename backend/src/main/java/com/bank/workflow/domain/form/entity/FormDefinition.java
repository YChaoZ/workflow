package com.bank.workflow.domain.form.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单定义实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class FormDefinition {
    
    /**
     * 表单ID
     */
    private Long id;
    
    /**
     * 表单Key（唯一标识）
     */
    private String formKey;
    
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
     * 版本号
     */
    private Integer version;
    
    /**
     * 表单分类ID
     */
    private Long categoryId;
    
    /**
     * 表单分类名称（冗余字段，非持久化）
     */
    private String categoryName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态（0:禁用 1:启用 2:草稿）
     */
    private Integer status;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新人
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否为草稿
     */
    public boolean isDraft() {
        return status != null && status == 2;
    }
    
    /**
     * 是否已启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    /**
     * 是否为内置表单
     */
    public boolean isBuiltIn() {
        return "BUILT_IN".equals(formType);
    }
}

