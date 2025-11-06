package com.bank.workflow.domain.definition.entity;

import lombok.Data;

import java.util.Date;

/**
 * 流程定义信息（领域实体）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessDefinitionInfo {
    
    /**
     * 流程定义ID
     */
    private String id;
    
    /**
     * 流程定义KEY（唯一标识）
     */
    private String key;
    
    /**
     * 流程定义名称
     */
    private String name;
    
    /**
     * 流程分类
     */
    private String category;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 部署ID
     */
    private String deploymentId;
    
    /**
     * 资源名称（BPMN文件名）
     */
    private String resourceName;
    
    /**
     * 是否挂起
     */
    private Boolean suspended;
    
    /**
     * 流程描述
     */
    private String description;
    
    /**
     * 部署时间
     */
    private Date deploymentTime;
}

