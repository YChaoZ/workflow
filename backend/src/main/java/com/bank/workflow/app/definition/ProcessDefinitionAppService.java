package com.bank.workflow.app.definition;

import com.bank.workflow.app.definition.command.DeployProcessCmd;
import com.bank.workflow.app.definition.dto.VersionCompareResult;
import com.bank.workflow.app.definition.query.ProcessDefinitionQuery;
import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo;
import com.bank.workflow.domain.process.gateway.ProcessEngineGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程定义应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessDefinitionAppService {
    
    private final ProcessEngineGateway processEngineGateway;
    
    /**
     * 部署流程定义
     */
    public String deployProcessDefinition(DeployProcessCmd cmd) {
        log.info("部署流程定义: cmd={}", cmd);
        
        // 参数校验
        if (cmd.getName() == null || cmd.getName().isEmpty()) {
            throw new IllegalArgumentException("流程名称不能为空");
        }
        if (cmd.getBpmnXml() == null || cmd.getBpmnXml().isEmpty()) {
            throw new IllegalArgumentException("BPMN XML不能为空");
        }
        
        // 调用网关部署
        return processEngineGateway.deployProcessDefinition(
            cmd.getName(),
            cmd.getCategory(),
            cmd.getBpmnXml(),
            cmd.getTenantId()
        );
    }
    
    /**
     * 删除流程部署
     */
    public void deleteDeployment(String deploymentId, Boolean cascade) {
        log.info("删除流程部署: deploymentId={}, cascade={}", deploymentId, cascade);
        
        if (deploymentId == null || deploymentId.isEmpty()) {
            throw new IllegalArgumentException("部署ID不能为空");
        }
        
        boolean cascadeDelete = cascade != null && cascade;
        processEngineGateway.deleteDeployment(deploymentId, cascadeDelete);
    }
    
    /**
     * 挂起流程定义
     */
    public void suspendProcessDefinition(String processDefinitionId) {
        log.info("挂起流程定义: processDefinitionId={}", processDefinitionId);
        
        if (processDefinitionId == null || processDefinitionId.isEmpty()) {
            throw new IllegalArgumentException("流程定义ID不能为空");
        }
        
        processEngineGateway.suspendProcessDefinition(processDefinitionId);
    }
    
    /**
     * 激活流程定义
     */
    public void activateProcessDefinition(String processDefinitionId) {
        log.info("激活流程定义: processDefinitionId={}", processDefinitionId);
        
        if (processDefinitionId == null || processDefinitionId.isEmpty()) {
            throw new IllegalArgumentException("流程定义ID不能为空");
        }
        
        processEngineGateway.activateProcessDefinition(processDefinitionId);
    }
    
    /**
     * 分页查询流程定义列表
     */
    public PageResult<ProcessDefinitionInfo> queryProcessDefinitions(ProcessDefinitionQuery queryParam) {
        log.info("查询流程定义列表: query={}", queryParam);
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("processKey", queryParam.getProcessKey());
        params.put("processName", queryParam.getProcessName());
        params.put("category", queryParam.getCategory());
        params.put("suspended", queryParam.getSuspended());
        params.put("latestVersion", queryParam.getLatestVersion());
        params.put("pageNum", queryParam.getPageNum());
        params.put("pageSize", queryParam.getPageSize());
        params.put("orderBy", queryParam.getOrderBy());
        params.put("orderDirection", queryParam.getOrderDirection());
        
        // 查询列表和总数
        List<ProcessDefinitionInfo> list = processEngineGateway.queryProcessDefinitions(params);
        Long total = processEngineGateway.countProcessDefinitions(params);
        
        return PageResult.of(list, total, queryParam.getPageNum(), queryParam.getPageSize());
    }
    
    /**
     * 查询流程定义详情
     */
    public ProcessDefinitionInfo getProcessDefinition(String processDefinitionId) {
        log.info("查询流程定义详情: processDefinitionId={}", processDefinitionId);
        
        if (processDefinitionId == null || processDefinitionId.isEmpty()) {
            throw new IllegalArgumentException("流程定义ID不能为空");
        }
        
        return processEngineGateway.getProcessDefinition(processDefinitionId);
    }
    
    /**
     * 获取流程定义的BPMN XML
     */
    public String getProcessDefinitionBpmnXml(String processDefinitionId) {
        log.info("获取流程定义BPMN XML: processDefinitionId={}", processDefinitionId);
        
        if (processDefinitionId == null || processDefinitionId.isEmpty()) {
            throw new IllegalArgumentException("流程定义ID不能为空");
        }
        
        return processEngineGateway.getProcessDefinitionBpmnXml(processDefinitionId);
    }
    
    // ==================== 流程版本管理方法 ====================
    
    /**
     * 查询指定流程KEY的所有版本
     */
    public List<ProcessDefinitionInfo> listProcessVersionsByKey(String processKey) {
        log.info("查询流程所有版本: processKey={}", processKey);
        
        if (processKey == null || processKey.isEmpty()) {
            throw new IllegalArgumentException("流程KEY不能为空");
        }
        
        return processEngineGateway.listProcessVersionsByKey(processKey);
    }
    
    /**
     * 获取流程定义的最新版本
     */
    public ProcessDefinitionInfo getLatestProcessVersion(String processKey) {
        log.info("查询流程最新版本: processKey={}", processKey);
        
        if (processKey == null || processKey.isEmpty()) {
            throw new IllegalArgumentException("流程KEY不能为空");
        }
        
        return processEngineGateway.getLatestProcessVersion(processKey);
    }
    
    /**
     * 对比两个版本的流程定义
     */
    public VersionCompareResult compareVersions(String processKey, Integer sourceVersion, Integer targetVersion) {
        log.info("对比流程版本: processKey={}, sourceVersion={}, targetVersion={}", 
                processKey, sourceVersion, targetVersion);
        
        // 参数校验
        if (processKey == null || processKey.isEmpty()) {
            throw new IllegalArgumentException("流程KEY不能为空");
        }
        if (sourceVersion == null) {
            throw new IllegalArgumentException("源版本号不能为空");
        }
        if (targetVersion == null) {
            throw new IllegalArgumentException("目标版本号不能为空");
        }
        
        // 查询两个版本的流程定义
        ProcessDefinitionInfo sourceDefinition = processEngineGateway.getProcessDefinitionByKeyAndVersion(
                processKey, sourceVersion);
        ProcessDefinitionInfo targetDefinition = processEngineGateway.getProcessDefinitionByKeyAndVersion(
                processKey, targetVersion);
        
        if (sourceDefinition == null) {
            throw new IllegalArgumentException("源版本不存在: " + sourceVersion);
        }
        if (targetDefinition == null) {
            throw new IllegalArgumentException("目标版本不存在: " + targetVersion);
        }
        
        // 获取BPMN XML
        String sourceXml = processEngineGateway.getProcessDefinitionBpmnXml(sourceDefinition.getId());
        String targetXml = processEngineGateway.getProcessDefinitionBpmnXml(targetDefinition.getId());
        
        // 构建对比结果
        VersionCompareResult result = new VersionCompareResult();
        result.setSourceVersion(sourceVersion);
        result.setTargetVersion(targetVersion);
        result.setSourceXml(sourceXml);
        result.setTargetXml(targetXml);
        
        // 简单对比（实际项目中可以使用更复杂的XML对比算法）
        boolean hasDifference = !sourceXml.equals(targetXml);
        result.setHasDifference(hasDifference);
        
        if (hasDifference) {
            result.setDifferenceDescription(
                    String.format("版本%d和版本%d的BPMN定义有差异，源XML长度=%d，目标XML长度=%d",
                            sourceVersion, targetVersion, sourceXml.length(), targetXml.length()));
        } else {
            result.setDifferenceDescription("两个版本的BPMN定义完全相同");
        }
        
        return result;
    }
    
    /**
     * 回滚到指定版本（重新部署该版本）
     */
    public String rollbackToVersion(String processKey, Integer version) {
        log.info("回滚到指定版本: processKey={}, version={}", processKey, version);
        
        // 参数校验
        if (processKey == null || processKey.isEmpty()) {
            throw new IllegalArgumentException("流程KEY不能为空");
        }
        if (version == null) {
            throw new IllegalArgumentException("版本号不能为空");
        }
        
        // 查询指定版本的流程定义
        ProcessDefinitionInfo definition = processEngineGateway.getProcessDefinitionByKeyAndVersion(
                processKey, version);
        if (definition == null) {
            throw new IllegalArgumentException("版本不存在: " + version);
        }
        
        // 获取BPMN XML
        String bpmnXml = processEngineGateway.getProcessDefinitionBpmnXml(definition.getId());
        if (bpmnXml == null || bpmnXml.isEmpty()) {
            throw new IllegalArgumentException("无法获取版本" + version + "的BPMN定义");
        }
        
        // 重新部署该版本（这会创建一个新版本）
        String deploymentId = processEngineGateway.deployProcessDefinition(
                definition.getName() + " (回滚自版本" + version + ")",
                definition.getCategory(),
                bpmnXml,
                null
        );
        
        log.info("回滚成功，新部署ID={}", deploymentId);
        return deploymentId;
    }
    
    // ==================== 流程图生成方法 ====================
    
    /**
     * 生成流程定义的流程图（SVG格式）
     */
    public String generateProcessDiagramSvg(String processDefinitionId) {
        log.info("生成流程定义流程图: processDefinitionId={}", processDefinitionId);
        
        if (processDefinitionId == null || processDefinitionId.isEmpty()) {
            throw new IllegalArgumentException("流程定义ID不能为空");
        }
        
        return processEngineGateway.generateProcessDiagramSvg(processDefinitionId);
    }
    
    /**
     * 生成流程实例的流程图（SVG格式，高亮已完成和当前节点）
     */
    public String generateProcessInstanceDiagramSvg(String processInstanceId) {
        log.info("生成流程实例流程图: processInstanceId={}", processInstanceId);
        
        if (processInstanceId == null || processInstanceId.isEmpty()) {
            throw new IllegalArgumentException("流程实例ID不能为空");
        }
        
        return processEngineGateway.generateProcessInstanceDiagramSvg(processInstanceId);
    }
}

