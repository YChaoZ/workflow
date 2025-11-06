package com.bank.workflow.domain.form.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程表单关联实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessForm {
    
    /**
     * 关联ID
     */
    private Long id;
    
    /**
     * 流程定义Key
     */
    private String processDefinitionKey;
    
    /**
     * 节点ID（为空表示全局表单）
     */
    private String nodeId;
    
    /**
     * 表单Key
     */
    private String formKey;
    
    /**
     * 表单版本
     */
    private Integer formVersion;
    
    /**
     * 是否必填
     */
    private Integer isRequired;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否为全局表单
     */
    public boolean isGlobal() {
        return nodeId == null || nodeId.isEmpty();
    }
}

