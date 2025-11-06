package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo;
import com.bank.workflow.domain.process.entity.HistoricProcessInstance;
import com.bank.workflow.domain.process.entity.ProcessInstance;
import com.bank.workflow.domain.process.gateway.ProcessEngineGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程引擎网关实现（基于Flowable）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessEngineGatewayImpl implements ProcessEngineGateway {
    
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final ProcessEngineConfiguration processEngineConfiguration;
    
    @Override
    public ProcessInstance startProcess(String processKey, String businessKey,
                                        Map<String, Object> variables, String startUser) {
        log.info("启动流程: processKey={}, businessKey={}, startUser={}", processKey, businessKey, startUser);
        
        try {
            // 使用Flowable API启动流程
            ProcessInstanceBuilder builder = runtimeService.createProcessInstanceBuilder()
                .processDefinitionKey(processKey)
                .businessKey(businessKey);
            
            // 设置流程变量
            if (variables != null && !variables.isEmpty()) {
                builder.variables(variables);
            }
            
            // 设置启动人
            if (startUser != null) {
                builder.variable("startUser", startUser);
            }
            
            org.flowable.engine.runtime.ProcessInstance flowableInstance = builder.start();
            
            // 转换为领域对象
            return convertToProcessInstance(flowableInstance);
        } catch (Exception e) {
            log.error("启动流程失败: processKey={}, error={}", processKey, e.getMessage(), e);
            throw new RuntimeException("启动流程失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void suspendProcessInstance(String instanceId) {
        log.info("挂起流程实例: instanceId={}", instanceId);
        
        try {
            runtimeService.suspendProcessInstanceById(instanceId);
        } catch (Exception e) {
            log.error("挂起流程实例失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("挂起流程实例失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void activateProcessInstance(String instanceId) {
        log.info("激活流程实例: instanceId={}", instanceId);
        
        try {
            runtimeService.activateProcessInstanceById(instanceId);
        } catch (Exception e) {
            log.error("激活流程实例失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("激活流程实例失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ProcessInstance getProcessInstance(String instanceId) {
        log.info("查询流程实例: instanceId={}", instanceId);
        
        try {
            org.flowable.engine.runtime.ProcessInstance flowableInstance = 
                runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult();
            
            if (flowableInstance == null) {
                log.warn("流程实例不存在: instanceId={}", instanceId);
                return null;
            }
            
            return convertToProcessInstance(flowableInstance);
        } catch (Exception e) {
            log.error("查询流程实例失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("查询流程实例失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ProcessInstance> queryProcessInstances(Map<String, Object> params) {
        log.info("查询流程实例列表: params={}", params);
        
        try {
            ProcessInstanceQuery query = buildProcessInstanceQuery(params);
            
            // 分页参数
            Integer pageNum = (Integer) params.get("pageNum");
            Integer pageSize = (Integer) params.get("pageSize");
            if (pageNum != null && pageSize != null) {
                int firstResult = (pageNum - 1) * pageSize;
                query.listPage(firstResult, pageSize);
            }
            
            // 排序
            String orderBy = (String) params.get("orderBy");
            String orderDirection = (String) params.get("orderDirection");
            if ("startTime".equals(orderBy)) {
                query.orderByStartTime();
                if ("asc".equalsIgnoreCase(orderDirection)) {
                    query.asc();
                } else {
                    query.desc();
                }
            }
            
            List<org.flowable.engine.runtime.ProcessInstance> flowableInstances = query.list();
            
            // 转换为领域对象
            List<ProcessInstance> result = new ArrayList<>();
            for (org.flowable.engine.runtime.ProcessInstance flowableInstance : flowableInstances) {
                result.add(convertToProcessInstance(flowableInstance));
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询流程实例列表失败: error={}", e.getMessage(), e);
            throw new RuntimeException("查询流程实例列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long countProcessInstances(Map<String, Object> params) {
        log.info("统计流程实例数量: params={}", params);
        
        try {
            ProcessInstanceQuery query = buildProcessInstanceQuery(params);
            return query.count();
        } catch (Exception e) {
            log.error("统计流程实例数量失败: error={}", e.getMessage(), e);
            throw new RuntimeException("统计流程实例数量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteProcessInstance(String instanceId, String reason) {
        log.info("删除流程实例: instanceId={}, reason={}", instanceId, reason);
        
        try {
            runtimeService.deleteProcessInstance(instanceId, reason);
        } catch (Exception e) {
            log.error("删除流程实例失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("删除流程实例失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建流程实例查询对象
     */
    private ProcessInstanceQuery buildProcessInstanceQuery(Map<String, Object> params) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        
        // 流程定义KEY
        String processKey = (String) params.get("processKey");
        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        
        // 业务KEY
        String businessKey = (String) params.get("businessKey");
        if (businessKey != null && !businessKey.isEmpty()) {
            query.processInstanceBusinessKey(businessKey);
        }
        
        // 发起人
        String startUser = (String) params.get("startUser");
        if (startUser != null && !startUser.isEmpty()) {
            query.variableValueEquals("startUser", startUser);
        }
        
        // 流程状态
        String state = (String) params.get("state");
        if ("active".equals(state)) {
            query.active();
        } else if ("suspended".equals(state)) {
            query.suspended();
        }
        
        return query;
    }
    
    /**
     * 转换Flowable流程实例为领域对象
     */
    private ProcessInstance convertToProcessInstance(org.flowable.engine.runtime.ProcessInstance flowableInstance) {
        ProcessInstance instance = new ProcessInstance();
        instance.setInstanceId(flowableInstance.getId());
        instance.setProcessDefinitionId(flowableInstance.getProcessDefinitionId());
        instance.setProcessKey(flowableInstance.getProcessDefinitionKey());
        instance.setBusinessKey(flowableInstance.getBusinessKey());
        instance.setProcessName(flowableInstance.getName());
        instance.setStartTime(flowableInstance.getStartTime());
        
        // 设置状态
        if (flowableInstance.isSuspended()) {
            instance.setStatus("SUSPENDED");
        } else if (flowableInstance.isEnded()) {
            instance.setStatus("COMPLETED");
        } else {
            instance.setStatus("RUNNING");
        }
        
        // 获取流程变量
        Map<String, Object> variables = runtimeService.getVariables(flowableInstance.getId());
        instance.setVariables(variables != null ? variables : new HashMap<>());
        
        // 设置启动人
        if (variables != null && variables.containsKey("startUser")) {
            instance.setStartUser(String.valueOf(variables.get("startUser")));
        }
        
        return instance;
    }
    
    // ==================== 历史查询方法实现 ====================
    
    @Override
    public List<HistoricProcessInstance> queryHistoricProcessInstances(Map<String, Object> params) {
        log.info("查询历史流程实例列表: params={}", params);
        
        try {
            HistoricProcessInstanceQuery query = buildHistoricProcessInstanceQuery(params);
            
            // 分页参数
            Integer pageNum = (Integer) params.get("pageNum");
            Integer pageSize = (Integer) params.get("pageSize");
            if (pageNum != null && pageSize != null) {
                int firstResult = (pageNum - 1) * pageSize;
                query.listPage(firstResult, pageSize);
            }
            
            // 排序
            String orderBy = (String) params.get("orderBy");
            String orderDirection = (String) params.get("orderDirection");
            if ("startTime".equals(orderBy)) {
                query.orderByProcessInstanceStartTime();
                if ("asc".equalsIgnoreCase(orderDirection)) {
                    query.asc();
                } else {
                    query.desc();
                }
            } else if ("endTime".equals(orderBy)) {
                query.orderByProcessInstanceEndTime();
                if ("asc".equalsIgnoreCase(orderDirection)) {
                    query.asc();
                } else {
                    query.desc();
                }
            }
            
            List<org.flowable.engine.history.HistoricProcessInstance> flowableInstances = query.list();
            List<HistoricProcessInstance> result = new ArrayList<>();
            for (org.flowable.engine.history.HistoricProcessInstance flowableInstance : flowableInstances) {
                result.add(convertToHistoricProcessInstance(flowableInstance));
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询历史流程实例列表失败: error={}", e.getMessage(), e);
            throw new RuntimeException("查询历史流程实例列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long countHistoricProcessInstances(Map<String, Object> params) {
        log.info("统计历史流程实例数量: params={}", params);
        
        try {
            HistoricProcessInstanceQuery query = buildHistoricProcessInstanceQuery(params);
            return query.count();
        } catch (Exception e) {
            log.error("统计历史流程实例数量失败: error={}", e.getMessage(), e);
            throw new RuntimeException("统计历史流程实例数量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public HistoricProcessInstance getHistoricProcessInstance(String instanceId) {
        log.info("查询历史流程实例详情: instanceId={}", instanceId);
        
        try {
            org.flowable.engine.history.HistoricProcessInstance flowableInstance = 
                historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .includeProcessVariables()
                    .singleResult();
            
            if (flowableInstance == null) {
                log.warn("历史流程实例不存在: instanceId={}", instanceId);
                return null;
            }
            
            return convertToHistoricProcessInstance(flowableInstance);
        } catch (Exception e) {
            log.error("查询历史流程实例详情失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("查询历史流程实例详情失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<com.bank.workflow.domain.process.entity.HistoricActivityInstance> queryHistoricActivityInstances(String instanceId) {
        log.info("查询流程执行轨迹: instanceId={}", instanceId);
        
        try {
            List<org.flowable.engine.history.HistoricActivityInstance> flowableActivities = 
                historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(instanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();
            
            List<com.bank.workflow.domain.process.entity.HistoricActivityInstance> result = new ArrayList<>();
            for (org.flowable.engine.history.HistoricActivityInstance flowableActivity : flowableActivities) {
                result.add(convertToHistoricActivityInstance(flowableActivity));
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询流程执行轨迹失败: instanceId={}, error={}", instanceId, e.getMessage(), e);
            throw new RuntimeException("查询流程执行轨迹失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建历史流程实例查询
     */
    private HistoricProcessInstanceQuery buildHistoricProcessInstanceQuery(Map<String, Object> params) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        
        // 流程定义KEY
        String processKey = (String) params.get("processKey");
        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        
        // 业务KEY
        String businessKey = (String) params.get("businessKey");
        if (businessKey != null && !businessKey.isEmpty()) {
            query.processInstanceBusinessKey(businessKey);
        }
        
        // 发起人
        String startUser = (String) params.get("startUser");
        if (startUser != null && !startUser.isEmpty()) {
            query.variableValueEquals("startUser", startUser);
        }
        
        // 是否已完成
        Boolean finished = (Boolean) params.get("finished");
        if (finished != null) {
            if (finished) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        
        // 开始时间范围
        String startTimeBegin = (String) params.get("startTimeBegin");
        String startTimeEnd = (String) params.get("startTimeEnd");
        if (startTimeBegin != null && !startTimeBegin.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeBegin);
                query.startedAfter(date);
            } catch (ParseException e) {
                log.warn("开始时间格式错误: {}", startTimeBegin);
            }
        }
        if (startTimeEnd != null && !startTimeEnd.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeEnd);
                query.startedBefore(date);
            } catch (ParseException e) {
                log.warn("开始时间格式错误: {}", startTimeEnd);
            }
        }
        
        // 结束时间范围
        String endTimeBegin = (String) params.get("endTimeBegin");
        String endTimeEnd = (String) params.get("endTimeEnd");
        if (endTimeBegin != null && !endTimeBegin.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeBegin);
                query.finishedAfter(date);
            } catch (ParseException e) {
                log.warn("结束时间格式错误: {}", endTimeBegin);
            }
        }
        if (endTimeEnd != null && !endTimeEnd.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeEnd);
                query.finishedBefore(date);
            } catch (ParseException e) {
                log.warn("结束时间格式错误: {}", endTimeEnd);
            }
        }
        
        return query;
    }
    
    /**
     * 转换Flowable历史流程实例为领域对象
     */
    private HistoricProcessInstance convertToHistoricProcessInstance(
            org.flowable.engine.history.HistoricProcessInstance flowableInstance) {
        HistoricProcessInstance instance = new HistoricProcessInstance();
        instance.setInstanceId(flowableInstance.getId());
        instance.setProcessDefinitionId(flowableInstance.getProcessDefinitionId());
        instance.setProcessKey(flowableInstance.getProcessDefinitionKey());
        instance.setBusinessKey(flowableInstance.getBusinessKey());
        instance.setProcessName(flowableInstance.getName());
        instance.setProcessVersion(flowableInstance.getProcessDefinitionVersion());
        instance.setStartTime(flowableInstance.getStartTime());
        instance.setEndTime(flowableInstance.getEndTime());
        instance.setDuration(flowableInstance.getDurationInMillis());
        instance.setDeleteReason(flowableInstance.getDeleteReason());
        
        // 设置流程变量
        Map<String, Object> variables = flowableInstance.getProcessVariables();
        instance.setVariables(variables != null ? variables : new HashMap<>());
        
        // 设置启动人
        if (variables != null && variables.containsKey("startUser")) {
            instance.setStartUser(String.valueOf(variables.get("startUser")));
        }
        
        return instance;
    }
    
    /**
     * 转换Flowable历史活动实例为领域对象
     */
    private com.bank.workflow.domain.process.entity.HistoricActivityInstance convertToHistoricActivityInstance(
            org.flowable.engine.history.HistoricActivityInstance flowableActivity) {
        com.bank.workflow.domain.process.entity.HistoricActivityInstance activity = new com.bank.workflow.domain.process.entity.HistoricActivityInstance();
        activity.setActivityId(flowableActivity.getActivityId());
        activity.setActivityName(flowableActivity.getActivityName());
        activity.setActivityType(flowableActivity.getActivityType());
        activity.setProcessInstanceId(flowableActivity.getProcessInstanceId());
        activity.setProcessDefinitionId(flowableActivity.getProcessDefinitionId());
        activity.setExecutionId(flowableActivity.getExecutionId());
        activity.setTaskId(flowableActivity.getTaskId());
        activity.setAssignee(flowableActivity.getAssignee());
        activity.setStartTime(flowableActivity.getStartTime());
        activity.setEndTime(flowableActivity.getEndTime());
        activity.setDuration(flowableActivity.getDurationInMillis());
        
        return activity;
    }
    
    // ==================== 流程定义管理方法实现 ====================
    
    @Override
    public String deployProcessDefinition(String name, String category, String bpmnXml, String tenantId) {
        log.info("部署流程定义: name={}, category={}, tenantId={}", name, category, tenantId);
        
        try {
            // 生成资源名称
            String resourceName = name + ".bpmn20.xml";
            
            // 创建部署
            org.flowable.engine.repository.DeploymentBuilder builder = repositoryService.createDeployment()
                .name(name)
                .category(category)
                .addString(resourceName, bpmnXml);
            
            // 设置租户ID（如果提供）
            if (tenantId != null && !tenantId.isEmpty()) {
                builder.tenantId(tenantId);
            }
            
            // 执行部署
            Deployment deployment = builder.deploy();
            
            log.info("流程定义部署成功: deploymentId={}", deployment.getId());
            return deployment.getId();
            
        } catch (Exception e) {
            log.error("部署流程定义失败: error={}", e.getMessage(), e);
            throw new RuntimeException("部署流程定义失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteDeployment(String deploymentId, boolean cascade) {
        log.info("删除流程部署: deploymentId={}, cascade={}", deploymentId, cascade);
        
        try {
            repositoryService.deleteDeployment(deploymentId, cascade);
            log.info("流程部署删除成功: deploymentId={}", deploymentId);
        } catch (Exception e) {
            log.error("删除流程部署失败: deploymentId={}, error={}", deploymentId, e.getMessage(), e);
            throw new RuntimeException("删除流程部署失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void suspendProcessDefinition(String processDefinitionId) {
        log.info("挂起流程定义: processDefinitionId={}", processDefinitionId);
        
        try {
            // suspendProcessInstancesById=true 表示同时挂起所有运行中的流程实例
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            log.info("流程定义挂起成功: processDefinitionId={}", processDefinitionId);
        } catch (Exception e) {
            log.error("挂起流程定义失败: processDefinitionId={}, error={}", processDefinitionId, e.getMessage(), e);
            throw new RuntimeException("挂起流程定义失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void activateProcessDefinition(String processDefinitionId) {
        log.info("激活流程定义: processDefinitionId={}", processDefinitionId);
        
        try {
            // activateProcessInstancesById=true 表示同时激活所有被挂起的流程实例
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            log.info("流程定义激活成功: processDefinitionId={}", processDefinitionId);
        } catch (Exception e) {
            log.error("激活流程定义失败: processDefinitionId={}, error={}", processDefinitionId, e.getMessage(), e);
            throw new RuntimeException("激活流程定义失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ProcessDefinitionInfo> queryProcessDefinitions(Map<String, Object> params) {
        log.info("查询流程定义列表: params={}", params);
        
        try {
            ProcessDefinitionQuery query = buildProcessDefinitionQuery(params);
            
            // 分页参数
            Integer pageNum = (Integer) params.get("pageNum");
            Integer pageSize = (Integer) params.get("pageSize");
            if (pageNum != null && pageSize != null) {
                int firstResult = (pageNum - 1) * pageSize;
                query.listPage(firstResult, pageSize);
            }
            
            // 排序
            String orderBy = (String) params.get("orderBy");
            String orderDirection = (String) params.get("orderDirection");
            if ("deploymentTime".equals(orderBy)) {
                query.orderByDeploymentId();
            } else if ("version".equals(orderBy)) {
                query.orderByProcessDefinitionVersion();
            } else {
                query.orderByDeploymentId(); // 默认按部署时间排序
            }
            
            if ("asc".equalsIgnoreCase(orderDirection)) {
                query.asc();
            } else {
                query.desc();
            }
            
            List<ProcessDefinition> flowableDefinitions = query.list();
            List<ProcessDefinitionInfo> result = new ArrayList<>();
            for (ProcessDefinition flowableDefinition : flowableDefinitions) {
                result.add(convertToProcessDefinitionInfo(flowableDefinition));
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询流程定义列表失败: error={}", e.getMessage(), e);
            throw new RuntimeException("查询流程定义列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Long countProcessDefinitions(Map<String, Object> params) {
        log.info("统计流程定义数量: params={}", params);
        
        try {
            ProcessDefinitionQuery query = buildProcessDefinitionQuery(params);
            return query.count();
        } catch (Exception e) {
            log.error("统计流程定义数量失败: error={}", e.getMessage(), e);
            throw new RuntimeException("统计流程定义数量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ProcessDefinitionInfo getProcessDefinition(String processDefinitionId) {
        log.info("查询流程定义详情: processDefinitionId={}", processDefinitionId);
        
        try {
            ProcessDefinition flowableDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
            
            if (flowableDefinition == null) {
                log.warn("流程定义不存在: processDefinitionId={}", processDefinitionId);
                return null;
            }
            
            return convertToProcessDefinitionInfo(flowableDefinition);
        } catch (Exception e) {
            log.error("查询流程定义详情失败: processDefinitionId={}, error={}", processDefinitionId, e.getMessage(), e);
            throw new RuntimeException("查询流程定义详情失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProcessDefinitionBpmnXml(String processDefinitionId) {
        log.info("获取流程定义BPMN XML: processDefinitionId={}", processDefinitionId);
        
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
            
            if (processDefinition == null) {
                log.warn("流程定义不存在: processDefinitionId={}", processDefinitionId);
                return null;
            }
            
            InputStream xmlStream = repositoryService.getProcessModel(processDefinitionId);
            if (xmlStream == null) {
                log.warn("流程定义XML不存在: processDefinitionId={}", processDefinitionId);
                return null;
            }
            
            byte[] xmlBytes = xmlStream.readAllBytes();
            String bpmnXml = new String(xmlBytes, StandardCharsets.UTF_8);
            
            log.info("获取流程定义BPMN XML成功: processDefinitionId={}, xmlLength={}", 
                    processDefinitionId, bpmnXml.length());
            return bpmnXml;
            
        } catch (Exception e) {
            log.error("获取流程定义BPMN XML失败: processDefinitionId={}, error={}", 
                    processDefinitionId, e.getMessage(), e);
            throw new RuntimeException("获取流程定义BPMN XML失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建流程定义查询
     */
    private ProcessDefinitionQuery buildProcessDefinitionQuery(Map<String, Object> params) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        
        // 流程定义KEY
        String processKey = (String) params.get("processKey");
        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        
        // 流程名称（模糊查询）
        String processName = (String) params.get("processName");
        if (processName != null && !processName.isEmpty()) {
            query.processDefinitionNameLike("%" + processName + "%");
        }
        
        // 流程分类
        String category = (String) params.get("category");
        if (category != null && !category.isEmpty()) {
            query.processDefinitionCategory(category);
        }
        
        // 是否挂起
        Boolean suspended = (Boolean) params.get("suspended");
        if (suspended != null) {
            if (suspended) {
                query.suspended();
            } else {
                query.active();
            }
        }
        
        // 是否只查询最新版本
        Boolean latestVersion = (Boolean) params.get("latestVersion");
        if (latestVersion != null && latestVersion) {
            query.latestVersion();
        }
        
        return query;
    }
    
    /**
     * 转换Flowable流程定义为领域对象
     */
    private ProcessDefinitionInfo convertToProcessDefinitionInfo(ProcessDefinition flowableDefinition) {
        ProcessDefinitionInfo info = new ProcessDefinitionInfo();
        info.setId(flowableDefinition.getId());
        info.setKey(flowableDefinition.getKey());
        info.setName(flowableDefinition.getName());
        info.setCategory(flowableDefinition.getCategory());
        info.setVersion(flowableDefinition.getVersion());
        info.setDeploymentId(flowableDefinition.getDeploymentId());
        info.setResourceName(flowableDefinition.getResourceName());
        info.setSuspended(flowableDefinition.isSuspended());
        info.setDescription(flowableDefinition.getDescription());
        
        // 获取部署时间
        try {
            Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(flowableDefinition.getDeploymentId())
                .singleResult();
            if (deployment != null) {
                info.setDeploymentTime(deployment.getDeploymentTime());
            }
        } catch (Exception e) {
            log.warn("获取部署时间失败: deploymentId={}", flowableDefinition.getDeploymentId());
        }
        
        return info;
    }
    
    // ==================== 流程版本管理方法实现 ====================
    
    @Override
    public List<ProcessDefinitionInfo> listProcessVersionsByKey(String processKey) {
        log.info("查询流程所有版本: processKey={}", processKey);
        
        try {
            List<ProcessDefinition> flowableDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
            
            List<ProcessDefinitionInfo> result = new ArrayList<>();
            for (ProcessDefinition flowableDefinition : flowableDefinitions) {
                result.add(convertToProcessDefinitionInfo(flowableDefinition));
            }
            
            log.info("查询到{}个版本", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询流程所有版本失败: processKey={}, error={}", processKey, e.getMessage(), e);
            throw new RuntimeException("查询流程所有版本失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ProcessDefinitionInfo getLatestProcessVersion(String processKey) {
        log.info("查询流程最新版本: processKey={}", processKey);
        
        try {
            ProcessDefinition flowableDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
            
            if (flowableDefinition == null) {
                log.warn("流程定义不存在: processKey={}", processKey);
                return null;
            }
            
            return convertToProcessDefinitionInfo(flowableDefinition);
        } catch (Exception e) {
            log.error("查询流程最新版本失败: processKey={}, error={}", processKey, e.getMessage(), e);
            throw new RuntimeException("查询流程最新版本失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ProcessDefinitionInfo getProcessDefinitionByKeyAndVersion(String processKey, Integer version) {
        log.info("根据KEY和版本号查询流程定义: processKey={}, version={}", processKey, version);
        
        try {
            ProcessDefinition flowableDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .processDefinitionVersion(version)
                .singleResult();
            
            if (flowableDefinition == null) {
                log.warn("流程定义不存在: processKey={}, version={}", processKey, version);
                return null;
            }
            
            return convertToProcessDefinitionInfo(flowableDefinition);
        } catch (Exception e) {
            log.error("根据KEY和版本号查询流程定义失败: processKey={}, version={}, error={}", 
                    processKey, version, e.getMessage(), e);
            throw new RuntimeException("根据KEY和版本号查询流程定义失败: " + e.getMessage(), e);
        }
    }
    
    // ==================== 流程图生成方法实现 ====================
    
    @Override
    public String generateProcessDiagramSvg(String processDefinitionId) {
        log.info("生成流程定义流程图: processDefinitionId={}", processDefinitionId);
        
        try {
            // 获取BPMN模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            if (bpmnModel == null) {
                throw new RuntimeException("流程定义不存在: " + processDefinitionId);
            }
            
            // 生成流程图（SVG格式）
            ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            InputStream svgStream = processDiagramGenerator.generateDiagram(
                    bpmnModel,
                    "svg",
                    Collections.emptyList(), // 高亮活动节点（空表示不高亮）
                    Collections.emptyList(), // 高亮流（空表示不高亮）
                    "微软雅黑", // 字体
                    "微软雅黑", // 字体
                    "微软雅黑", // 字体
                    null, // ClassLoader
                    1.0,  // 缩放比例
                    true  // 生成默认图标
            );
            
            // 转换为字符串
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = svgStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            String svg = outputStream.toString(StandardCharsets.UTF_8);
            
            log.info("流程图生成成功: processDefinitionId={}, svgLength={}", 
                    processDefinitionId, svg.length());
            return svg;
            
        } catch (Exception e) {
            log.error("生成流程定义流程图失败: processDefinitionId={}, error={}", 
                    processDefinitionId, e.getMessage(), e);
            throw new RuntimeException("生成流程定义流程图失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateProcessInstanceDiagramSvg(String processInstanceId) {
        log.info("生成流程实例流程图: processInstanceId={}", processInstanceId);
        
        try {
            // 获取流程实例
            org.flowable.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            
            String processDefinitionId;
            if (processInstance != null) {
                processDefinitionId = processInstance.getProcessDefinitionId();
            } else {
                // 如果运行时实例不存在，尝试从历史查询
                org.flowable.engine.history.HistoricProcessInstance historicProcessInstance = 
                        historyService.createHistoricProcessInstanceQuery()
                                .processInstanceId(processInstanceId)
                                .singleResult();
                if (historicProcessInstance == null) {
                    throw new RuntimeException("流程实例不存在: " + processInstanceId);
                }
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            }
            
            // 获取BPMN模型
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            if (bpmnModel == null) {
                throw new RuntimeException("流程定义不存在: " + processDefinitionId);
            }
            
            // 查询历史活动实例，获取已完成的节点
            List<org.flowable.engine.history.HistoricActivityInstance> historicActivityInstances = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();
            
            // 提取高亮节点（已完成的活动节点）
            List<String> highLightedActivities = historicActivityInstances.stream()
                    .filter(activity -> activity.getActivityType() != null)
                    .map(org.flowable.engine.history.HistoricActivityInstance::getActivityId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 提取高亮流（已完成的连线）
            List<String> highLightedFlows = new ArrayList<>();
            
            // 生成流程图（SVG格式）
            ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            InputStream svgStream = processDiagramGenerator.generateDiagram(
                    bpmnModel,
                    "svg",
                    highLightedActivities, // 高亮活动节点
                    highLightedFlows,      // 高亮流
                    "微软雅黑", // 字体
                    "微软雅黑", // 字体
                    "微软雅黑", // 字体
                    null, // ClassLoader
                    1.0,  // 缩放比例
                    true  // 生成默认图标
            );
            
            // 转换为字符串
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = svgStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            String svg = outputStream.toString(StandardCharsets.UTF_8);
            
            log.info("流程实例流程图生成成功: processInstanceId={}, svgLength={}", 
                    processInstanceId, svg.length());
            return svg;
            
        } catch (Exception e) {
            log.error("生成流程实例流程图失败: processInstanceId={}, error={}", 
                    processInstanceId, e.getMessage(), e);
            throw new RuntimeException("生成流程实例流程图失败: " + e.getMessage(), e);
        }
    }
}

