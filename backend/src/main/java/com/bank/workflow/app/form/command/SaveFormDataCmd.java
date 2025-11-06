package com.bank.workflow.app.form.command;

import lombok.Data;

/**
 * 保存表单数据命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class SaveFormDataCmd {
    
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
}

