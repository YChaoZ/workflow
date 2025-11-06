package com.bank.workflow.adapter.web;

import com.bank.workflow.app.definition.ProcessDefinitionAppService;
import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.process.ProcessAppService;
import com.bank.workflow.app.process.command.StartProcessCmd;
import com.bank.workflow.app.process.query.HistoricProcessInstanceQuery;
import com.bank.workflow.app.process.query.ProcessInstanceQuery;
import com.bank.workflow.domain.process.entity.HistoricActivityInstance;
import com.bank.workflow.domain.process.entity.HistoricProcessInstance;
import com.bank.workflow.domain.process.entity.ProcessInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {
    
    private final ProcessAppService processAppService;
    private final ProcessDefinitionAppService processDefinitionAppService;
    
    /**
     * 启动流程
     */
    @PostMapping("/start")
    public Map<String, Object> startProcess(@RequestBody StartProcessCmd cmd) {
        log.info("接收启动流程请求: processKey={}", cmd.getProcessKey());
        
        String instanceId = processAppService.startProcess(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程启动成功");
        result.put("data", instanceId);
        return result;
    }
    
    /**
     * 分页查询流程实例列表
     */
    @GetMapping("/instances")
    public Map<String, Object> queryProcessInstances(ProcessInstanceQuery query) {
        log.info("接收查询流程实例列表请求: query={}", query);
        
        PageResult<ProcessInstance> pageResult = processAppService.queryProcessInstances(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }
    
    /**
     * 查询单个流程实例
     */
    @GetMapping("/instance/{instanceId}")
    public Map<String, Object> getProcessInstance(@PathVariable String instanceId) {
        log.info("接收查询流程实例请求: instanceId={}", instanceId);
        
        ProcessInstance instance = processAppService.getProcessInstance(instanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", instance);
        return result;
    }
    
    /**
     * 挂起流程实例
     */
    @PostMapping("/instance/{instanceId}/suspend")
    public Map<String, Object> suspendProcessInstance(@PathVariable String instanceId) {
        log.info("接收挂起流程实例请求: instanceId={}", instanceId);
        
        processAppService.suspendProcessInstance(instanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程已挂起");
        return result;
    }
    
    /**
     * 激活流程实例
     */
    @PostMapping("/instance/{instanceId}/activate")
    public Map<String, Object> activateProcessInstance(@PathVariable String instanceId) {
        log.info("接收激活流程实例请求: instanceId={}", instanceId);
        
        processAppService.activateProcessInstance(instanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程已激活");
        return result;
    }
    
    /**
     * 删除流程实例
     */
    @DeleteMapping("/instance/{instanceId}")
    public Map<String, Object> deleteProcessInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false, defaultValue = "用户删除") String reason) {
        log.info("接收删除流程实例请求: instanceId={}, reason={}", instanceId, reason);
        
        processAppService.deleteProcessInstance(instanceId, reason);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程已删除");
        return result;
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "服务正常");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    // ==================== 历史查询接口 ====================
    
    /**
     * 分页查询历史流程实例列表
     */
    @GetMapping("/history/instances")
    public Map<String, Object> queryHistoricProcessInstances(HistoricProcessInstanceQuery query) {
        log.info("接收查询历史流程实例列表请求: query={}", query);
        
        PageResult<HistoricProcessInstance> pageResult = processAppService.queryHistoricProcessInstances(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }
    
    /**
     * 查询单个历史流程实例
     */
    @GetMapping("/history/instance/{instanceId}")
    public Map<String, Object> getHistoricProcessInstance(@PathVariable String instanceId) {
        log.info("接收查询历史流程实例详情请求: instanceId={}", instanceId);
        
        HistoricProcessInstance instance = processAppService.getHistoricProcessInstance(instanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", instance);
        return result;
    }
    
    /**
     * 查询流程执行轨迹（历史活动实例）
     */
    @GetMapping("/history/instance/{instanceId}/activities")
    public Map<String, Object> queryHistoricActivityInstances(@PathVariable String instanceId) {
        log.info("接收查询流程执行轨迹请求: instanceId={}", instanceId);
        
        List<HistoricActivityInstance> activities = processAppService.queryHistoricActivityInstances(instanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", activities);
        return result;
    }
    
    /**
     * 生成流程实例流程图（SVG格式，高亮已完成和当前节点）
     */
    @GetMapping("/instance/{processInstanceId}/diagram")
    public Map<String, Object> generateProcessInstanceDiagramSvg(@PathVariable String processInstanceId) {
        log.info("接收生成流程实例流程图请求: processInstanceId={}", processInstanceId);
        
        String svg = processDefinitionAppService.generateProcessInstanceDiagramSvg(processInstanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "流程图生成成功");
        result.put("data", Map.of("svg", svg));
        return result;
    }
}

