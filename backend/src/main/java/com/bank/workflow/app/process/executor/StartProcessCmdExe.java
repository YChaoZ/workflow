package com.bank.workflow.app.process.executor;

import com.bank.workflow.app.process.command.StartProcessCmd;
import com.bank.workflow.domain.process.entity.ProcessInstance;
import com.bank.workflow.domain.process.gateway.ProcessEngineGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 启动流程命令执行器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartProcessCmdExe {
    
    private final ProcessEngineGateway processEngineGateway;
    
    @Transactional(rollbackFor = Exception.class)
    public String execute(StartProcessCmd cmd) {
        log.info("执行启动流程命令: processKey={}, businessKey={}", cmd.getProcessKey(), cmd.getBusinessKey());
        
        // 1. 参数校验
        validateCommand(cmd);
        
        // 2. 调用领域服务启动流程
        ProcessInstance instance = processEngineGateway.startProcess(
            cmd.getProcessKey(),
            cmd.getBusinessKey(),
            cmd.getVariables(),
            cmd.getStartUser()
        );
        
        // 3. 返回流程实例ID
        log.info("流程启动成功: instanceId={}", instance.getInstanceId());
        return instance.getInstanceId();
    }
    
    private void validateCommand(StartProcessCmd cmd) {
        if (cmd.getProcessKey() == null || cmd.getProcessKey().trim().isEmpty()) {
            throw new IllegalArgumentException("流程定义KEY不能为空");
        }
        if (cmd.getStartUser() == null || cmd.getStartUser().trim().isEmpty()) {
            throw new IllegalArgumentException("启动人不能为空");
        }
    }
}

