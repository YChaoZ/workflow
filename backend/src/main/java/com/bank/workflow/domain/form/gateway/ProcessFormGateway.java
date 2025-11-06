package com.bank.workflow.domain.form.gateway;

import com.bank.workflow.domain.form.entity.ProcessForm;

import java.util.List;

/**
 * 流程表单关联网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface ProcessFormGateway {
    
    /**
     * 创建流程表单关联
     *
     * @param processForm 流程表单关联
     * @return 关联ID
     */
    Long createProcessForm(ProcessForm processForm);
    
    /**
     * 更新流程表单关联
     *
     * @param processForm 流程表单关联
     */
    void updateProcessForm(ProcessForm processForm);
    
    /**
     * 删除流程表单关联
     *
     * @param id 关联ID
     */
    void deleteProcessForm(Long id);
    
    /**
     * 根据ID查询流程表单关联
     *
     * @param id 关联ID
     * @return 流程表单关联
     */
    ProcessForm getProcessFormById(Long id);
    
    /**
     * 根据流程定义Key查询所有表单关联
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程表单关联列表
     */
    List<ProcessForm> listProcessFormsByProcessKey(String processDefinitionKey);
    
    /**
     * 根据流程定义Key和节点ID查询表单关联
     *
     * @param processDefinitionKey 流程定义Key
     * @param nodeId               节点ID
     * @return 流程表单关联
     */
    ProcessForm getProcessFormByProcessKeyAndNodeId(String processDefinitionKey, String nodeId);
    
    /**
     * 查询流程的全局表单
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程表单关联
     */
    ProcessForm getGlobalFormByProcessKey(String processDefinitionKey);
}

