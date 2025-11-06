package com.bank.workflow.app.attachment;

import com.bank.workflow.app.attachment.command.CreateAttachmentCmd;
import com.bank.workflow.domain.attachment.entity.TaskAttachment;
import com.bank.workflow.domain.attachment.gateway.TaskAttachmentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务附件应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAttachmentAppService {
    
    private final TaskAttachmentGateway taskAttachmentGateway;
    
    /**
     * 创建任务附件
     */
    public Long createAttachment(CreateAttachmentCmd cmd) {
        log.info("创建任务附件: cmd={}", cmd);
        
        // 参数校验
        if (cmd.getTaskId() == null || cmd.getTaskId().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (cmd.getFileName() == null || cmd.getFileName().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (cmd.getFilePath() == null || cmd.getFilePath().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        
        // 转换为领域实体
        TaskAttachment attachment = new TaskAttachment();
        BeanUtils.copyProperties(cmd, attachment);
        
        // 创建附件
        return taskAttachmentGateway.createAttachment(attachment);
    }
    
    /**
     * 删除任务附件
     */
    public void deleteAttachment(Long attachmentId) {
        log.info("删除任务附件: attachmentId={}", attachmentId);
        
        if (attachmentId == null) {
            throw new IllegalArgumentException("附件ID不能为空");
        }
        
        taskAttachmentGateway.deleteAttachment(attachmentId);
    }
    
    /**
     * 查询附件详情
     */
    public TaskAttachment getAttachment(Long attachmentId) {
        log.info("查询任务附件详情: attachmentId={}", attachmentId);
        
        if (attachmentId == null) {
            throw new IllegalArgumentException("附件ID不能为空");
        }
        
        return taskAttachmentGateway.getAttachment(attachmentId);
    }
    
    /**
     * 查询任务附件列表
     */
    public List<TaskAttachment> listAttachmentsByTaskId(String taskId) {
        log.info("查询任务附件列表: taskId={}", taskId);
        
        if (taskId == null || taskId.isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        return taskAttachmentGateway.listAttachmentsByTaskId(taskId);
    }
    
    /**
     * 查询流程实例的所有附件
     */
    public List<TaskAttachment> listAttachmentsByProcessInstanceId(String processInstanceId) {
        log.info("查询流程实例附件列表: processInstanceId={}", processInstanceId);
        
        if (processInstanceId == null || processInstanceId.isEmpty()) {
            throw new IllegalArgumentException("流程实例ID不能为空");
        }
        
        return taskAttachmentGateway.listAttachmentsByProcessInstanceId(processInstanceId);
    }
}
