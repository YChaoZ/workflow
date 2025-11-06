package com.bank.workflow.domain.process.gateway;

import com.bank.workflow.domain.process.entity.HistoricActivityInstance;
import com.bank.workflow.domain.process.entity.HistoricProcessInstance;
import com.bank.workflow.domain.process.entity.ProcessInstance;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎网关接口
 * 由 Infrastructure 层实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface ProcessEngineGateway {
    
    /**
     * 启动流程
     *
     * @param processKey 流程定义KEY
     * @param businessKey 业务KEY
     * @param variables 流程变量
     * @param startUser 启动人
     * @return 流程实例
     */
    ProcessInstance startProcess(String processKey, String businessKey, 
                                  Map<String, Object> variables, String startUser);
    
    /**
     * 查询流程实例
     *
     * @param instanceId 流程实例ID
     * @return 流程实例
     */
    ProcessInstance getProcessInstance(String instanceId);
    
    /**
     * 查询流程实例列表
     *
     * @param params 查询参数
     * @return 流程实例列表
     */
    List<ProcessInstance> queryProcessInstances(Map<String, Object> params);
    
    /**
     * 统计流程实例数量
     *
     * @param params 查询参数
     * @return 实例数量
     */
    Long countProcessInstances(Map<String, Object> params);
    
    /**
     * 挂起流程实例
     *
     * @param instanceId 流程实例ID
     */
    void suspendProcessInstance(String instanceId);
    
    /**
     * 激活流程实例
     *
     * @param instanceId 流程实例ID
     */
    void activateProcessInstance(String instanceId);
    
    /**
     * 删除流程实例
     *
     * @param instanceId 流程实例ID
     * @param reason 删除原因
     */
    void deleteProcessInstance(String instanceId, String reason);
    
    // ==================== 历史查询接口 ====================
    
    /**
     * 查询历史流程实例列表
     *
     * @param params 查询参数
     * @return 历史流程实例列表
     */
    List<HistoricProcessInstance> queryHistoricProcessInstances(Map<String, Object> params);
    
    /**
     * 统计历史流程实例数量
     *
     * @param params 查询参数
     * @return 实例数量
     */
    Long countHistoricProcessInstances(Map<String, Object> params);
    
    /**
     * 查询单个历史流程实例
     *
     * @param instanceId 流程实例ID
     * @return 历史流程实例
     */
    HistoricProcessInstance getHistoricProcessInstance(String instanceId);
    
    /**
     * 查询流程执行轨迹（历史活动实例）
     *
     * @param instanceId 流程实例ID
     * @return 历史活动实例列表
     */
    List<HistoricActivityInstance> queryHistoricActivityInstances(String instanceId);
    
    // ==================== 流程定义管理接口 ====================
    
    /**
     * 部署流程定义
     *
     * @param name 部署名称
     * @param category 流程分类
     * @param bpmnXml BPMN XML内容
     * @param tenantId 租户ID
     * @return 部署ID
     */
    String deployProcessDefinition(String name, String category, String bpmnXml, String tenantId);
    
    /**
     * 删除流程部署
     *
     * @param deploymentId 部署ID
     * @param cascade 是否级联删除（删除流程实例）
     */
    void deleteDeployment(String deploymentId, boolean cascade);
    
    /**
     * 挂起流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    void suspendProcessDefinition(String processDefinitionId);
    
    /**
     * 激活流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    void activateProcessDefinition(String processDefinitionId);
    
    /**
     * 查询流程定义列表
     *
     * @param params 查询参数
     * @return 流程定义列表
     */
    List<com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo> queryProcessDefinitions(Map<String, Object> params);
    
    /**
     * 统计流程定义数量
     *
     * @param params 查询参数
     * @return 定义数量
     */
    Long countProcessDefinitions(Map<String, Object> params);
    
    /**
     * 获取流程定义详情
     *
     * @param processDefinitionId 流程定义ID
     * @return 流程定义信息
     */
    com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo getProcessDefinition(String processDefinitionId);
    
    /**
     * 获取流程定义的BPMN XML
     *
     * @param processDefinitionId 流程定义ID
     * @return BPMN XML内容
     */
    String getProcessDefinitionBpmnXml(String processDefinitionId);
    
    // ==================== 流程版本管理接口 ====================
    
    /**
     * 查询指定流程KEY的所有版本
     *
     * @param processKey 流程定义KEY
     * @return 流程定义列表（按版本号降序）
     */
    List<com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo> listProcessVersionsByKey(String processKey);
    
    /**
     * 获取流程定义的最新版本
     *
     * @param processKey 流程定义KEY
     * @return 流程定义信息
     */
    com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo getLatestProcessVersion(String processKey);
    
    /**
     * 根据KEY和版本号查询流程定义
     *
     * @param processKey 流程定义KEY
     * @param version 版本号
     * @return 流程定义信息
     */
    com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo getProcessDefinitionByKeyAndVersion(String processKey, Integer version);
    
    // ==================== 流程图生成接口 ====================
    
    /**
     * 生成流程定义的流程图（SVG格式）
     *
     * @param processDefinitionId 流程定义ID
     * @return SVG格式的流程图
     */
    String generateProcessDiagramSvg(String processDefinitionId);
    
    /**
     * 生成流程实例的流程图（SVG格式，高亮已完成和当前节点）
     *
     * @param processInstanceId 流程实例ID
     * @return SVG格式的流程图
     */
    String generateProcessInstanceDiagramSvg(String processInstanceId);
}

