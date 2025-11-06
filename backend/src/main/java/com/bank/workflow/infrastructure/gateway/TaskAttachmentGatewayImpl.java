package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.attachment.entity.TaskAttachment;
import com.bank.workflow.domain.attachment.gateway.TaskAttachmentGateway;
import com.bank.workflow.infrastructure.persistence.mapper.TaskAttachmentMapper;
import com.bank.workflow.infrastructure.persistence.po.TaskAttachmentDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务附件网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAttachmentGatewayImpl implements TaskAttachmentGateway {
    
    private final TaskAttachmentMapper taskAttachmentMapper;
    
    @Override
    public Long createAttachment(TaskAttachment attachment) {
        log.info("创建任务附件: attachment={}", attachment);
        
        TaskAttachmentDO attachmentDO = new TaskAttachmentDO();
        BeanUtils.copyProperties(attachment, attachmentDO);
        attachmentDO.setUploadedTime(new Date());
        
        taskAttachmentMapper.insert(attachmentDO);
        
        log.info("任务附件创建成功: attachmentId={}", attachmentDO.getId());
        return attachmentDO.getId();
    }
    
    @Override
    public void deleteAttachment(Long attachmentId) {
        log.info("删除任务附件: attachmentId={}", attachmentId);
        
        taskAttachmentMapper.deleteById(attachmentId);
        
        log.info("任务附件删除成功: attachmentId={}", attachmentId);
    }
    
    @Override
    public TaskAttachment getAttachment(Long attachmentId) {
        log.info("查询任务附件详情: attachmentId={}", attachmentId);
        
        TaskAttachmentDO attachmentDO = taskAttachmentMapper.selectById(attachmentId);
        if (attachmentDO == null) {
            log.warn("任务附件不存在: attachmentId={}", attachmentId);
            return null;
        }
        
        return convertToEntity(attachmentDO);
    }
    
    @Override
    public List<TaskAttachment> listAttachmentsByTaskId(String taskId) {
        log.info("查询任务附件列表: taskId={}", taskId);
        
        LambdaQueryWrapper<TaskAttachmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskAttachmentDO::getTaskId, taskId);
        wrapper.orderByAsc(TaskAttachmentDO::getUploadedTime);
        
        List<TaskAttachmentDO> attachmentDOList = taskAttachmentMapper.selectList(wrapper);
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachmentDO attachmentDO : attachmentDOList) {
            result.add(convertToEntity(attachmentDO));
        }
        
        log.info("查询到{}个任务附件", result.size());
        return result;
    }
    
    @Override
    public List<TaskAttachment> listAttachmentsByProcessInstanceId(String processInstanceId) {
        log.info("查询流程实例附件列表: processInstanceId={}", processInstanceId);
        
        LambdaQueryWrapper<TaskAttachmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskAttachmentDO::getProcessInstanceId, processInstanceId);
        wrapper.orderByAsc(TaskAttachmentDO::getUploadedTime);
        
        List<TaskAttachmentDO> attachmentDOList = taskAttachmentMapper.selectList(wrapper);
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachmentDO attachmentDO : attachmentDOList) {
            result.add(convertToEntity(attachmentDO));
        }
        
        log.info("查询到{}个流程附件", result.size());
        return result;
    }
    
    /**
     * 转换DO为领域实体
     */
    private TaskAttachment convertToEntity(TaskAttachmentDO attachmentDO) {
        TaskAttachment attachment = new TaskAttachment();
        BeanUtils.copyProperties(attachmentDO, attachment);
        return attachment;
    }
}
