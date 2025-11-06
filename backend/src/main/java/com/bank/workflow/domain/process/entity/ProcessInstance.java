package com.bank.workflow.domain.process.entity;

import lombok.Data;
import java.util.Date;
import java.util.Map;

/**
 * 流程实例领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class ProcessInstance {
    
    /** 流程实例ID */
    private String instanceId;
    
    /** 流程定义ID */
    private String processDefinitionId;
    
    /** 流程定义KEY */
    private String processKey;
    
    /** 业务KEY */
    private String businessKey;
    
    /** 流程名称 */
    private String processName;
    
    /** 流程状态：RUNNING, SUSPENDED, COMPLETED, TERMINATED */
    private String status;
    
    /** 启动人 */
    private String startUser;
    
    /** 启动时间 */
    private Date startTime;
    
    /** 结束时间 */
    private Date endTime;
    
    /** 流程变量 */
    private Map<String, Object> variables;
    
    /**
     * 挂起流程
     */
    public void suspend() {
        if ("COMPLETED".equals(this.status)) {
            throw new IllegalStateException("已完成的流程不能挂起");
        }
        this.status = "SUSPENDED";
    }
    
    /**
     * 激活流程
     */
    public void activate() {
        if (!"SUSPENDED".equals(this.status)) {
            throw new IllegalStateException("只有挂起的流程才能激活");
        }
        this.status = "RUNNING";
    }
    
    /**
     * 完成流程
     */
    public void complete() {
        this.status = "COMPLETED";
        this.endTime = new Date();
    }
    
    /**
     * 判断流程是否正在运行
     */
    public boolean isRunning() {
        return "RUNNING".equals(this.status);
    }
    
    /**
     * 判断流程是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }
}

