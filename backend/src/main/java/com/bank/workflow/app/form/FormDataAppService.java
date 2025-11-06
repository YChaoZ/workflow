package com.bank.workflow.app.form;

import com.bank.workflow.app.form.command.SaveFormDataCmd;
import com.bank.workflow.domain.form.entity.FormData;
import com.bank.workflow.domain.form.gateway.FormDataGateway;
import com.bank.workflow.domain.form.gateway.FormDefinitionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 表单数据应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormDataAppService {

    private final FormDataGateway formDataGateway;
    private final FormDefinitionGateway formDefinitionGateway;

    /**
     * 保存表单数据
     *
     * @param cmd 保存命令
     * @return 数据ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveFormData(SaveFormDataCmd cmd) {
        log.info("接收保存表单数据请求: cmd={}", cmd);

        // 检查表单是否存在
        if (formDefinitionGateway.getFormDefinitionById(cmd.getFormId()) == null) {
            throw new IllegalArgumentException("表单不存在: " + cmd.getFormId());
        }

        // 检查是否已存在数据（任务ID唯一）
        if (cmd.getTaskId() != null) {
            FormData existing = formDataGateway.getFormDataByTaskId(cmd.getTaskId());
            if (existing != null) {
                // 更新已有数据
                existing.setFormData(cmd.getFormData());
                existing.setSubmitUser(cmd.getSubmitUser());
                formDataGateway.updateFormData(existing);
                log.info("表单数据更新成功: dataId={}", existing.getId());
                return existing.getId();
            }
        }

        // 创建新数据
        FormData formData = new FormData();
        BeanUtils.copyProperties(cmd, formData);

        Long dataId = formDataGateway.saveFormData(formData);

        log.info("表单数据保存成功: dataId={}", dataId);
        return dataId;
    }

    /**
     * 根据ID查询表单数据
     *
     * @param dataId 数据ID
     * @return 表单数据
     */
    public FormData getFormDataById(Long dataId) {
        log.info("根据ID查询表单数据: dataId={}", dataId);
        return formDataGateway.getFormDataById(dataId);
    }

    /**
     * 根据流程实例ID查询表单数据
     *
     * @param processInstanceId 流程实例ID
     * @return 表单数据
     */
    public FormData getFormDataByProcessInstanceId(String processInstanceId) {
        log.info("根据流程实例ID查询表单数据: processInstanceId={}", processInstanceId);
        return formDataGateway.getFormDataByProcessInstanceId(processInstanceId);
    }

    /**
     * 根据任务ID查询表单数据
     *
     * @param taskId 任务ID
     * @return 表单数据
     */
    public FormData getFormDataByTaskId(String taskId) {
        log.info("根据任务ID查询表单数据: taskId={}", taskId);
        return formDataGateway.getFormDataByTaskId(taskId);
    }

    /**
     * 根据表单ID查询所有数据
     *
     * @param formId 表单ID
     * @return 表单数据列表
     */
    public List<FormData> listFormDataByFormId(Long formId) {
        log.info("根据表单ID查询所有数据: formId={}", formId);
        return formDataGateway.listFormDataByFormId(formId);
    }

    /**
     * 删除表单数据
     *
     * @param dataId 数据ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormData(Long dataId) {
        log.info("接收删除表单数据请求: dataId={}", dataId);

        // 检查数据是否存在
        FormData existing = formDataGateway.getFormDataById(dataId);
        if (existing == null) {
            throw new IllegalArgumentException("表单数据不存在: " + dataId);
        }

        formDataGateway.deleteFormData(dataId);

        log.info("表单数据删除成功: dataId={}", dataId);
    }
}

