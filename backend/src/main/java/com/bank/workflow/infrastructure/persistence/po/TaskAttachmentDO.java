package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务附件持久化对象
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("wf_task_attachment")
public class TaskAttachmentDO {
    
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
