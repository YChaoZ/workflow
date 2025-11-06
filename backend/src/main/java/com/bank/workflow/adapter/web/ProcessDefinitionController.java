package com.bank.workflow.adapter.web;

import com.bank.workflow.app.definition.ProcessDefinitionAppService;
import com.bank.workflow.app.definition.command.DeployProcessCmd;
import com.bank.workflow.app.definition.dto.VersionCompareResult;
import com.bank.workflow.app.definition.query.ProcessDefinitionQuery;
import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.domain.definition.entity.ProcessDefinitionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程定义控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/process/definition")
@RequiredArgsConstructor
public class ProcessDefinitionController {
    
    private final ProcessDefinitionAppService processDefinitionAppService;
    
    /**
     * 部署流程定义
     */
    @PostMapping("/deploy")
    public Map<String, Object> deployProcessDefinition(@RequestBody DeployProcessCmd cmd) {
        log.info("接收部署流程定义请求: cmd={}", cmd);
        
        String deploymentId = processDefinitionAppService.deployProcessDefinition(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "部署成功");
        result.put("data", Map.of("deploymentId", deploymentId));
        return result;
    }
    
    /**
     * 删除流程部署
     */
    @DeleteMapping("/deployment/{deploymentId}")
    public Map<String, Object> deleteDeployment(
            @PathVariable String deploymentId,
            @RequestParam(required = false, defaultValue = "false") Boolean cascade) {
        log.info("接收删除流程部署请求: deploymentId={}, cascade={}", deploymentId, cascade);
        
        processDefinitionAppService.deleteDeployment(deploymentId, cascade);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 挂起流程定义
     */
    @PutMapping("/{processDefinitionId}/suspend")
    public Map<String, Object> suspendProcessDefinition(@PathVariable String processDefinitionId) {
        log.info("接收挂起流程定义请求: processDefinitionId={}", processDefinitionId);
        
        processDefinitionAppService.suspendProcessDefinition(processDefinitionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "挂起成功");
        return result;
    }
    
    /**
     * 激活流程定义
     */
    @PutMapping("/{processDefinitionId}/activate")
    public Map<String, Object> activateProcessDefinition(@PathVariable String processDefinitionId) {
        log.info("接收激活流程定义请求: processDefinitionId={}", processDefinitionId);
        
        processDefinitionAppService.activateProcessDefinition(processDefinitionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "激活成功");
        return result;
    }
    
    /**
     * 分页查询流程定义列表
     */
    @GetMapping("/list")
    public Map<String, Object> queryProcessDefinitions(ProcessDefinitionQuery query) {
        log.info("接收查询流程定义列表请求: query={}", query);
        
        PageResult<ProcessDefinitionInfo> pageResult = processDefinitionAppService.queryProcessDefinitions(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }
    
    /**
     * 查询流程定义详情
     */
    @GetMapping("/{processDefinitionId}")
    public Map<String, Object> getProcessDefinition(@PathVariable String processDefinitionId) {
        log.info("接收查询流程定义详情请求: processDefinitionId={}", processDefinitionId);
        
        ProcessDefinitionInfo info = processDefinitionAppService.getProcessDefinition(processDefinitionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", info);
        return result;
    }
    
    /**
     * 获取流程定义的BPMN XML
     */
    @GetMapping("/{processDefinitionId}/xml")
    public Map<String, Object> getProcessDefinitionBpmnXml(@PathVariable String processDefinitionId) {
        log.info("接收获取流程定义BPMN XML请求: processDefinitionId={}", processDefinitionId);
        
        String bpmnXml = processDefinitionAppService.getProcessDefinitionBpmnXml(processDefinitionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", Map.of("bpmnXml", bpmnXml));
        return result;
    }
    
    // ==================== 流程版本管理接口 ====================
    
    /**
     * 查询指定流程KEY的所有版本
     */
    @GetMapping("/versions/{processKey}")
    public Map<String, Object> listProcessVersionsByKey(@PathVariable String processKey) {
        log.info("接收查询流程所有版本请求: processKey={}", processKey);
        
        List<ProcessDefinitionInfo> versions = processDefinitionAppService.listProcessVersionsByKey(processKey);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", versions);
        return result;
    }
    
    /**
     * 获取流程定义的最新版本
     */
    @GetMapping("/latest/{processKey}")
    public Map<String, Object> getLatestProcessVersion(@PathVariable String processKey) {
        log.info("接收查询流程最新版本请求: processKey={}", processKey);
        
        ProcessDefinitionInfo latestVersion = processDefinitionAppService.getLatestProcessVersion(processKey);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", latestVersion);
        return result;
    }
    
    /**
     * 对比两个版本的流程定义
     */
    @GetMapping("/compare/{processKey}")
    public Map<String, Object> compareVersions(
            @PathVariable String processKey,
            @RequestParam Integer sourceVersion,
            @RequestParam Integer targetVersion) {
        log.info("接收对比流程版本请求: processKey={}, sourceVersion={}, targetVersion={}", 
                processKey, sourceVersion, targetVersion);
        
        VersionCompareResult compareResult = processDefinitionAppService.compareVersions(
                processKey, sourceVersion, targetVersion);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "对比成功");
        result.put("data", compareResult);
        return result;
    }
    
    /**
     * 回滚到指定版本
     */
    @PostMapping("/rollback/{processKey}")
    public Map<String, Object> rollbackToVersion(
            @PathVariable String processKey,
            @RequestParam Integer version) {
        log.info("接收回滚流程版本请求: processKey={}, version={}", processKey, version);
        
        String deploymentId = processDefinitionAppService.rollbackToVersion(processKey, version);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "回滚成功");
        result.put("data", Map.of("deploymentId", deploymentId));
        return result;
    }
    
    // ==================== 流程图生成接口 ====================
    
    /**
     * 生成流程定义的流程图（SVG格式）
     */
    @GetMapping("/{processDefinitionId}/diagram")
    public Map<String, Object> generateProcessDiagramSvg(@PathVariable String processDefinitionId) {
        log.info("接收生成流程定义流程图请求: processDefinitionId={}", processDefinitionId);
        
        String svg = processDefinitionAppService.generateProcessDiagramSvg(processDefinitionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程图生成成功");
        result.put("data", Map.of("svg", svg));
        return result;
    }
}

