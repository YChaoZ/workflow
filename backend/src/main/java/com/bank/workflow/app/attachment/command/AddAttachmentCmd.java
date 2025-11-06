package com.bank.workflow.app.attachment.command;

import lombok.Data;

/**
 * 添加任务附件命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class AddAttachmentCmd {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    
    /**
     * 附件名称
     */
    private String fileName;
    
    /**
     * 附件类型（MIME类型）
     */
    private String fileType;
    
    /**
     * 附件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件存储路径
     */
    private String filePath;
    
    /**
     * 上传人用户ID
     */
    private String userId;
    
    /**
     * 上传人姓名
     */
    private String userName;
}

