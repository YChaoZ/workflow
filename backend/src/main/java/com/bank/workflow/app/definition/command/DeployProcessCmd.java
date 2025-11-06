package com.bank.workflow.app.definition.command;

import lombok.Data;

/**
 * 部署流程定义命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class DeployProcessCmd {
    
    /**
     * 流程定义名称
     */
    private String name;
    
    /**
     * 流程分类
     */
    private String category;
    
    /**
     * BPMN XML内容
     */
    private String bpmnXml;
    
    /**
     * 流程KEY（可选，如果不提供则从XML中解析）
     */
    private String processKey;
    
    /**
     * 租户ID（可选）
     */
    private String tenantId;
}

