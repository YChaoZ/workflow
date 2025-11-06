package com.bank.workflow.app.process;

import com.bank.workflow.app.dto.PageResult;
import com.bank.workflow.app.process.command.StartProcessCmd;
import com.bank.workflow.app.process.executor.StartProcessCmdExe;
import com.bank.workflow.app.process.query.HistoricProcessInstanceQuery;
import com.bank.workflow.app.process.query.ProcessInstanceQuery;
import com.bank.workflow.domain.process.entity.HistoricActivityInstance;
import com.bank.workflow.domain.process.entity.HistoricProcessInstance;
import com.bank.workflow.domain.process.entity.ProcessInstance;
import com.bank.workflow.domain.process.gateway.ProcessEngineGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAppService {
    
    private final StartProcessCmdExe startProcessCmdExe;
    private final ProcessEngineGateway processEngineGateway;
    
    /**
     * 启动流程
     */
    public String startProcess(StartProcessCmd cmd) {
        return startProcessCmdExe.execute(cmd);
    }
    
    /**
     * 查询流程实例
     */
    public ProcessInstance getProcessInstance(String instanceId) {
        return processEngineGateway.getProcessInstance(instanceId);
    }
    
    /**
     * 分页查询流程实例列表
     */
    public PageResult<ProcessInstance> queryProcessInstances(ProcessInstanceQuery queryParam) {
        log.info("查询流程实例列表: query={}", queryParam);
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("processKey", queryParam.getProcessKey());
        params.put("businessKey", queryParam.getBusinessKey());
        params.put("startUser", queryParam.getStartUser());
        params.put("state", queryParam.getState());
        params.put("pageNum", queryParam.getPageNum());
        params.put("pageSize", queryParam.getPageSize());
        params.put("orderBy", queryParam.getOrderBy());
        params.put("orderDirection", queryParam.getOrderDirection());
        
        // 查询列表和总数
        List<ProcessInstance> list = processEngineGateway.queryProcessInstances(params);
        Long total = processEngineGateway.countProcessInstances(params);
        
        return PageResult.of(list, total, queryParam.getPageNum(), queryParam.getPageSize());
    }
    
    /**
     * 挂起流程实例
     */
    public void suspendProcessInstance(String instanceId) {
        processEngineGateway.suspendProcessInstance(instanceId);
    }
    
    /**
     * 激活流程实例
     */
    public void activateProcessInstance(String instanceId) {
        processEngineGateway.activateProcessInstance(instanceId);
    }
    
    /**
     * 删除流程实例
     */
    public void deleteProcessInstance(String instanceId, String reason) {
        processEngineGateway.deleteProcessInstance(instanceId, reason);
    }
    
    // ==================== 历史查询方法 ====================
    
    /**
     * 分页查询历史流程实例列表
     */
    public PageResult<HistoricProcessInstance> queryHistoricProcessInstances(HistoricProcessInstanceQuery queryParam) {
        log.info("查询历史流程实例列表: query={}", queryParam);
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("processKey", queryParam.getProcessKey());
        params.put("businessKey", queryParam.getBusinessKey());
        params.put("startUser", queryParam.getStartUser());
        params.put("finished", queryParam.getFinished());
        params.put("startTimeBegin", queryParam.getStartTimeBegin());
        params.put("startTimeEnd", queryParam.getStartTimeEnd());
        params.put("endTimeBegin", queryParam.getEndTimeBegin());
        params.put("endTimeEnd", queryParam.getEndTimeEnd());
        params.put("pageNum", queryParam.getPageNum());
        params.put("pageSize", queryParam.getPageSize());
        params.put("orderBy", queryParam.getOrderBy());
        params.put("orderDirection", queryParam.getOrderDirection());
        
        // 查询列表和总数
        List<HistoricProcessInstance> list = processEngineGateway.queryHistoricProcessInstances(params);
        Long total = processEngineGateway.countHistoricProcessInstances(params);
        
        return PageResult.of(list, total, queryParam.getPageNum(), queryParam.getPageSize());
    }
    
    /**
     * 查询单个历史流程实例
     */
    public HistoricProcessInstance getHistoricProcessInstance(String instanceId) {
        log.info("查询历史流程实例详情: instanceId={}", instanceId);
        return processEngineGateway.getHistoricProcessInstance(instanceId);
    }
    
    /**
     * 查询流程执行轨迹
     */
    public List<HistoricActivityInstance> queryHistoricActivityInstances(String instanceId) {
        log.info("查询流程执行轨迹: instanceId={}", instanceId);
        return processEngineGateway.queryHistoricActivityInstances(instanceId);
    }
}

