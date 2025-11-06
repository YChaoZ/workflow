package com.bank.workflow.domain.attachment.entity;

import lombok.Data;

import java.util.Date;

/**
 * 任务附件领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class TaskAttachment {
    
    /**
     * 附件ID
     */
    private Long id;
    
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
     * 附件类型
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
     * 上传人ID
     */
    private Long uploadedBy;
    
    /**
     * 上传时间
     */
    private Date uploadedTime;
}
