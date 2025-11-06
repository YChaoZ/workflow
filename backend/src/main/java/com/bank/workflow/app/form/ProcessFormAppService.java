package com.bank.workflow.app.form;

import com.bank.workflow.domain.form.entity.ProcessForm;
import com.bank.workflow.domain.form.gateway.FormDefinitionGateway;
import com.bank.workflow.domain.form.gateway.ProcessFormGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 流程表单关联应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessFormAppService {

    private final ProcessFormGateway processFormGateway;
    private final FormDefinitionGateway formDefinitionGateway;

    /**
     * 绑定表单到流程
     *
     * @param processForm 流程表单关联
     * @return 关联ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long bindFormToProcess(ProcessForm processForm) {
        log.info("接收绑定表单到流程请求: processForm={}", processForm);

        // 检查表单是否存在
        if (formDefinitionGateway.getFormDefinitionByKey(processForm.getFormKey()) == null) {
            throw new IllegalArgumentException("表单不存在: " + processForm.getFormKey());
        }

        // 检查是否已存在绑定
        ProcessForm existing;
        if (processForm.getNodeId() == null || processForm.getNodeId().isEmpty()) {
            existing = processFormGateway.getGlobalFormByProcessKey(processForm.getProcessDefinitionKey());
        } else {
            existing = processFormGateway.getProcessFormByProcessKeyAndNodeId(
                    processForm.getProcessDefinitionKey(), processForm.getNodeId());
        }

        if (existing != null) {
            throw new IllegalArgumentException("该流程节点已绑定表单");
        }

        Long id = processFormGateway.createProcessForm(processForm);

        log.info("表单绑定成功: id={}", id);
        return id;
    }

    /**
     * 更新流程表单关联
     *
     * @param processForm 流程表单关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessForm(ProcessForm processForm) {
        log.info("接收更新流程表单关联请求: processForm={}", processForm);

        // 检查关联是否存在
        ProcessForm existing = processFormGateway.getProcessFormById(processForm.getId());
        if (existing == null) {
            throw new IllegalArgumentException("流程表单关联不存在: " + processForm.getId());
        }

        // 检查表单是否存在
        if (formDefinitionGateway.getFormDefinitionByKey(processForm.getFormKey()) == null) {
            throw new IllegalArgumentException("表单不存在: " + processForm.getFormKey());
        }

        processFormGateway.updateProcessForm(processForm);

        log.info("流程表单关联更新成功: id={}", processForm.getId());
    }

    /**
     * 解绑表单
     *
     * @param id 关联ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindForm(Long id) {
        log.info("接收解绑表单请求: id={}", id);

        // 检查关联是否存在
        ProcessForm existing = processFormGateway.getProcessFormById(id);
        if (existing == null) {
            throw new IllegalArgumentException("流程表单关联不存在: " + id);
        }

        processFormGateway.deleteProcessForm(id);

        log.info("表单解绑成功: id={}", id);
    }

    /**
     * 根据ID查询流程表单关联
     *
     * @param id 关联ID
     * @return 流程表单关联
     */
    public ProcessForm getProcessFormById(Long id) {
        log.info("根据ID查询流程表单关联: id={}", id);
        return processFormGateway.getProcessFormById(id);
    }

    /**
     * 根据流程定义Key查询所有表单关联
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程表单关联列表
     */
    public List<ProcessForm> listProcessFormsByProcessKey(String processDefinitionKey) {
        log.info("根据流程定义Key查询所有表单关联: processKey={}", processDefinitionKey);
        return processFormGateway.listProcessFormsByProcessKey(processDefinitionKey);
    }

    /**
     * 根据流程定义Key和节点ID查询表单关联
     *
     * @param processDefinitionKey 流程定义Key
     * @param nodeId               节点ID
     * @return 流程表单关联
     */
    public ProcessForm getProcessFormByProcessKeyAndNodeId(String processDefinitionKey, String nodeId) {
        log.info("根据流程定义Key和节点ID查询表单关联: processKey={}, nodeId={}", 
                processDefinitionKey, nodeId);
        return processFormGateway.getProcessFormByProcessKeyAndNodeId(processDefinitionKey, nodeId);
    }

    /**
     * 查询流程的全局表单
     *
     * @param processDefinitionKey 流程定义Key
     * @return 流程表单关联
     */
    public ProcessForm getGlobalFormByProcessKey(String processDefinitionKey) {
        log.info("查询流程的全局表单: processKey={}", processDefinitionKey);
        return processFormGateway.getGlobalFormByProcessKey(processDefinitionKey);
    }
}

