package com.bank.workflow.domain.attachment.gateway;

import com.bank.workflow.domain.attachment.entity.TaskAttachment;

import java.util.List;

/**
 * 任务附件网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface TaskAttachmentGateway {
    
    /**
     * 创建任务附件
     *
     * @param attachment 任务附件
     * @return 附件ID
     */
    Long createAttachment(TaskAttachment attachment);
    
    /**
     * 删除任务附件
     *
     * @param attachmentId 附件ID
     */
    void deleteAttachment(Long attachmentId);
    
    /**
     * 查询附件详情
     *
     * @param attachmentId 附件ID
     * @return 附件信息
     */
    TaskAttachment getAttachment(Long attachmentId);
    
    /**
     * 查询任务附件列表
     *
     * @param taskId 任务ID
     * @return 附件列表
     */
    List<TaskAttachment> listAttachmentsByTaskId(String taskId);
    
    /**
     * 查询流程实例的所有附件
     *
     * @param processInstanceId 流程实例ID
     * @return 附件列表
     */
    List<TaskAttachment> listAttachmentsByProcessInstanceId(String processInstanceId);
}
