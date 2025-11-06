package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.comment.entity.TaskComment;
import com.bank.workflow.domain.comment.gateway.TaskCommentGateway;
import com.bank.workflow.infrastructure.persistence.mapper.TaskCommentMapper;
import com.bank.workflow.infrastructure.persistence.po.TaskCommentDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务意见网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCommentGatewayImpl implements TaskCommentGateway {
    
    private final TaskCommentMapper taskCommentMapper;
    
    @Override
    public Long createComment(TaskComment comment) {
        log.info("创建任务意见: comment={}", comment);
        
        TaskCommentDO commentDO = new TaskCommentDO();
        BeanUtils.copyProperties(comment, commentDO);
        commentDO.setCreatedTime(new Date());
        
        taskCommentMapper.insert(commentDO);
        
        log.info("任务意见创建成功: commentId={}", commentDO.getId());
        return commentDO.getId();
    }
    
    @Override
    public void deleteComment(Long commentId) {
        log.info("删除任务意见: commentId={}", commentId);
        
        taskCommentMapper.deleteById(commentId);
        
        log.info("任务意见删除成功: commentId={}", commentId);
    }
    
    @Override
    public List<TaskComment> listCommentsByTaskId(String taskId) {
        log.info("查询任务意见列表: taskId={}", taskId);
        
        LambdaQueryWrapper<TaskCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskCommentDO::getTaskId, taskId);
        wrapper.orderByAsc(TaskCommentDO::getCreatedTime);
        
        List<TaskCommentDO> commentDOList = taskCommentMapper.selectList(wrapper);
        List<TaskComment> result = new ArrayList<>();
        for (TaskCommentDO commentDO : commentDOList) {
            result.add(convertToEntity(commentDO));
        }
        
        log.info("查询到{}条任务意见", result.size());
        return result;
    }
    
    @Override
    public List<TaskComment> listCommentsByProcessInstanceId(String processInstanceId) {
        log.info("查询流程实例意见列表: processInstanceId={}", processInstanceId);
        
        LambdaQueryWrapper<TaskCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskCommentDO::getProcessInstanceId, processInstanceId);
        wrapper.orderByAsc(TaskCommentDO::getCreatedTime);
        
        List<TaskCommentDO> commentDOList = taskCommentMapper.selectList(wrapper);
        List<TaskComment> result = new ArrayList<>();
        for (TaskCommentDO commentDO : commentDOList) {
            result.add(convertToEntity(commentDO));
        }
        
        log.info("查询到{}条流程意见", result.size());
        return result;
    }
    
    /**
     * 转换DO为领域实体
     */
    private TaskComment convertToEntity(TaskCommentDO commentDO) {
        TaskComment comment = new TaskComment();
        BeanUtils.copyProperties(commentDO, comment);
        return comment;
    }
}
