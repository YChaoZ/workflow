package com.bank.workflow.app.form;

import com.bank.workflow.app.form.command.CreateFormDefinitionCmd;
import com.bank.workflow.app.form.command.UpdateFormDefinitionCmd;
import com.bank.workflow.domain.form.entity.FormDefinition;
import com.bank.workflow.domain.form.gateway.FormDefinitionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 表单定义应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormDefinitionAppService {

    private final FormDefinitionGateway formDefinitionGateway;

    /**
     * 创建表单定义
     *
     * @param cmd 创建命令
     * @return 表单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createFormDefinition(CreateFormDefinitionCmd cmd) {
        log.info("接收创建表单定义请求: cmd={}", cmd);

        // 检查表单Key是否已存在
        FormDefinition existing = formDefinitionGateway.getFormDefinitionByKey(cmd.getFormKey());
        if (existing != null) {
            throw new IllegalArgumentException("表单Key已存在: " + cmd.getFormKey());
        }

        // 创建表单定义实体
        FormDefinition formDefinition = new FormDefinition();
        BeanUtils.copyProperties(cmd, formDefinition);
        formDefinition.setVersion(1); // 初始版本为1

        // 保存
        Long formId = formDefinitionGateway.createFormDefinition(formDefinition);

        log.info("表单定义创建成功: formId={}, formKey={}", formId, cmd.getFormKey());
        return formId;
    }

    /**
     * 更新表单定义
     *
     * @param cmd 更新命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFormDefinition(UpdateFormDefinitionCmd cmd) {
        log.info("接收更新表单定义请求: cmd={}", cmd);

        // 检查表单是否存在
        FormDefinition existing = formDefinitionGateway.getFormDefinitionById(cmd.getId());
        if (existing == null) {
            throw new IllegalArgumentException("表单定义不存在: " + cmd.getId());
        }

        // 内置表单不允许修改
        if (existing.isBuiltIn()) {
            throw new IllegalArgumentException("内置表单不允许修改");
        }

        // 更新表单定义
        FormDefinition formDefinition = new FormDefinition();
        BeanUtils.copyProperties(cmd, formDefinition);

        formDefinitionGateway.updateFormDefinition(formDefinition);

        log.info("表单定义更新成功: formId={}", cmd.getId());
    }

    /**
     * 删除表单定义
     *
     * @param formId 表单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormDefinition(Long formId) {
        log.info("接收删除表单定义请求: formId={}", formId);

        // 检查表单是否存在
        FormDefinition existing = formDefinitionGateway.getFormDefinitionById(formId);
        if (existing == null) {
            throw new IllegalArgumentException("表单定义不存在: " + formId);
        }

        // 内置表单不允许删除
        if (existing.isBuiltIn()) {
            throw new IllegalArgumentException("内置表单不允许删除");
        }

        // TODO: 检查是否有流程使用该表单

        formDefinitionGateway.deleteFormDefinition(formId);

        log.info("表单定义删除成功: formId={}", formId);
    }

    /**
     * 根据ID查询表单定义
     *
     * @param formId 表单ID
     * @return 表单定义
     */
    public FormDefinition getFormDefinitionById(Long formId) {
        log.info("根据ID查询表单定义: formId={}", formId);
        return formDefinitionGateway.getFormDefinitionById(formId);
    }

    /**
     * 根据FormKey查询表单定义（最新版本）
     *
     * @param formKey 表单Key
     * @return 表单定义
     */
    public FormDefinition getFormDefinitionByKey(String formKey) {
        log.info("根据FormKey查询表单定义: formKey={}", formKey);
        return formDefinitionGateway.getFormDefinitionByKey(formKey);
    }

    /**
     * 查询所有表单定义
     *
     * @return 表单定义列表
     */
    public List<FormDefinition> listAllFormDefinitions() {
        log.info("查询所有表单定义");
        return formDefinitionGateway.listAllFormDefinitions();
    }

    /**
     * 根据分类查询表单定义
     *
     * @param categoryId 分类ID
     * @return 表单定义列表
     */
    public List<FormDefinition> listFormDefinitionsByCategory(Long categoryId) {
        log.info("根据分类查询表单定义: categoryId={}", categoryId);
        return formDefinitionGateway.listFormDefinitionsByCategory(categoryId);
    }

    /**
     * 根据状态查询表单定义
     *
     * @param status 状态
     * @return 表单定义列表
     */
    public List<FormDefinition> listFormDefinitionsByStatus(Integer status) {
        log.info("根据状态查询表单定义: status={}", status);
        return formDefinitionGateway.listFormDefinitionsByStatus(status);
    }

    /**
     * 查询表单的所有版本
     *
     * @param formKey 表单Key
     * @return 表单定义列表
     */
    public List<FormDefinition> listFormVersions(String formKey) {
        log.info("查询表单的所有版本: formKey={}", formKey);
        return formDefinitionGateway.listFormVersions(formKey);
    }

    /**
     * 发布表单（创建新版本）
     *
     * @param formKey 表单Key
     * @return 新版本号
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer publishFormVersion(String formKey) {
        log.info("发布表单新版本: formKey={}", formKey);

        // 查询当前草稿版本
        FormDefinition draft = formDefinitionGateway.getFormDefinitionByKey(formKey);
        if (draft == null) {
            throw new IllegalArgumentException("表单不存在: " + formKey);
        }

        if (!draft.isDraft()) {
            throw new IllegalArgumentException("只能发布草稿状态的表单");
        }

        // 生成新版本号
        Integer newVersion = formDefinitionGateway.publishFormVersion(formKey);

        // 创建新版本
        FormDefinition newForm = new FormDefinition();
        BeanUtils.copyProperties(draft, newForm);
        newForm.setId(null);
        newForm.setVersion(newVersion);
        newForm.setStatus(1); // 启用状态

        formDefinitionGateway.createFormDefinition(newForm);

        log.info("表单发布成功: formKey={}, newVersion={}", formKey, newVersion);
        return newVersion;
    }
}

