package com.bank.workflow.domain.form.gateway;

import com.bank.workflow.domain.form.entity.FormData;

import java.util.List;

/**
 * 表单数据网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface FormDataGateway {
    
    /**
     * 保存表单数据
     *
     * @param formData 表单数据
     * @return 数据ID
     */
    Long saveFormData(FormData formData);
    
    /**
     * 更新表单数据
     *
     * @param formData 表单数据
     */
    void updateFormData(FormData formData);
    
    /**
     * 根据ID查询表单数据
     *
     * @param dataId 数据ID
     * @return 表单数据
     */
    FormData getFormDataById(Long dataId);
    
    /**
     * 根据流程实例ID查询表单数据
     *
     * @param processInstanceId 流程实例ID
     * @return 表单数据
     */
    FormData getFormDataByProcessInstanceId(String processInstanceId);
    
    /**
     * 根据任务ID查询表单数据
     *
     * @param taskId 任务ID
     * @return 表单数据
     */
    FormData getFormDataByTaskId(String taskId);
    
    /**
     * 根据表单ID查询所有数据
     *
     * @param formId 表单ID
     * @return 表单数据列表
     */
    List<FormData> listFormDataByFormId(Long formId);
    
    /**
     * 删除表单数据
     *
     * @param dataId 数据ID
     */
    void deleteFormData(Long dataId);
}

