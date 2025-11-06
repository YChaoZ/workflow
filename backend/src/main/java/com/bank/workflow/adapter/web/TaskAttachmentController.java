package com.bank.workflow.adapter.web;

import com.bank.workflow.app.attachment.TaskAttachmentAppService;
import com.bank.workflow.app.attachment.command.CreateAttachmentCmd;
import com.bank.workflow.domain.attachment.entity.TaskAttachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务附件控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/task/attachment")
@RequiredArgsConstructor
public class TaskAttachmentController {
    
    private final TaskAttachmentAppService taskAttachmentAppService;
    
    /**
     * 创建任务附件
     */
    @PostMapping
    public Map<String, Object> createAttachment(@RequestBody CreateAttachmentCmd cmd) {
        log.info("接收创建任务附件请求: cmd={}", cmd);
        
        Long attachmentId = taskAttachmentAppService.createAttachment(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "上传成功");
        result.put("data", Map.of("attachmentId", attachmentId));
        return result;
    }
    
    /**
     * 删除任务附件
     */
    @DeleteMapping("/{attachmentId}")
    public Map<String, Object> deleteAttachment(@PathVariable Long attachmentId) {
        log.info("接收删除任务附件请求: attachmentId={}", attachmentId);
        
        taskAttachmentAppService.deleteAttachment(attachmentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 查询附件详情
     */
    @GetMapping("/{attachmentId}")
    public Map<String, Object> getAttachment(@PathVariable Long attachmentId) {
        log.info("接收查询任务附件详情请求: attachmentId={}", attachmentId);
        
        TaskAttachment attachment = taskAttachmentAppService.getAttachment(attachmentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", attachment);
        return result;
    }
    
    /**
     * 查询任务附件列表
     */
    @GetMapping("/task/{taskId}")
    public Map<String, Object> listAttachmentsByTaskId(@PathVariable String taskId) {
        log.info("接收查询任务附件列表请求: taskId={}", taskId);
        
        List<TaskAttachment> attachments = taskAttachmentAppService.listAttachmentsByTaskId(taskId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", attachments);
        return result;
    }
    
    /**
     * 查询流程实例的所有附件
     */
    @GetMapping("/process/{processInstanceId}")
    public Map<String, Object> listAttachmentsByProcessInstanceId(@PathVariable String processInstanceId) {
        log.info("接收查询流程实例附件列表请求: processInstanceId={}", processInstanceId);
        
        List<TaskAttachment> attachments = taskAttachmentAppService.listAttachmentsByProcessInstanceId(processInstanceId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", attachments);
        return result;
    }
}
