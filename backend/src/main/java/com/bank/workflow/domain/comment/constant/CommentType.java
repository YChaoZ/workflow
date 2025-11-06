package com.bank.workflow.domain.comment.constant;

/**
 * 任务意见类型常量
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public class CommentType {
    
    /**
     * 同意
     */
    public static final String APPROVE = "APPROVE";
    
    /**
     * 拒绝
     */
    public static final String REJECT = "REJECT";
    
    /**
     * 转办
     */
    public static final String TRANSFER = "TRANSFER";
    
    private CommentType() {
        // 工具类，不允许实例化
    }
}

