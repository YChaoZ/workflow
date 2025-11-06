package com.bank.workflow.domain.form.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表单数据实体（匹配实际数据库结构）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class FormData {
    
    /**
     * 数据ID
     */
    private Long id;
    
    /**
     * 表单ID
     */
    private Long formId;
    
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 表单数据JSON
     */
    private String formData;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 提交人
     */
    private String submitUser;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 逻辑删除标记
     */
    private Integer deleted;
}

