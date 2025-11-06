package com.bank.workflow.app.process.command;

import lombok.Data;

import java.util.Map;

/**
 * 启动流程命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class StartProcessCmd {
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 流程变量 */
    private Map<String, Object> variables;
    
    /** 启动人 */
    private String startUser;
    
    /** 流程标题 */
    private String title;
}

