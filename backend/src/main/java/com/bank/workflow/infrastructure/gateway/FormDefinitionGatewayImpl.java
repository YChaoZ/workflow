package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.form.entity.FormDefinition;
import com.bank.workflow.domain.form.gateway.FormDefinitionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * 表单定义网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FormDefinitionGatewayImpl implements FormDefinitionGateway {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createFormDefinition(FormDefinition formDefinition) {
        log.info("创建表单定义: formKey={}", formDefinition.getFormKey());
        
        String sql = "INSERT INTO wf_form_definition (form_key, form_name, form_type, form_config, " +
                     "version, category_id, description, status, create_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, formDefinition.getFormKey());
            ps.setString(2, formDefinition.getFormName());
            ps.setString(3, formDefinition.getFormType());
            ps.setString(4, formDefinition.getFormConfig());
            ps.setInt(5, formDefinition.getVersion() != null ? formDefinition.getVersion() : 1);
            ps.setObject(6, formDefinition.getCategoryId());
            ps.setString(7, formDefinition.getDescription());
            ps.setInt(8, formDefinition.getStatus() != null ? formDefinition.getStatus() : 1);
            ps.setString(9, formDefinition.getCreateBy());
            return ps;
        }, keyHolder);
        
        Long formId = keyHolder.getKey().longValue();
        log.info("表单定义创建成功: formId={}", formId);
        return formId;
    }

    @Override
    public void updateFormDefinition(FormDefinition formDefinition) {
        log.info("更新表单定义: formId={}", formDefinition.getId());
        
        String sql = "UPDATE wf_form_definition SET form_name = ?, form_type = ?, form_config = ?, " +
                     "category_id = ?, description = ?, status = ?, update_by = ? " +
                     "WHERE id = ? AND deleted = 0";
        
        int rows = jdbcTemplate.update(sql,
                formDefinition.getFormName(),
                formDefinition.getFormType(),
                formDefinition.getFormConfig(),
                formDefinition.getCategoryId(),
                formDefinition.getDescription(),
                formDefinition.getStatus(),
                formDefinition.getUpdateBy(),
                formDefinition.getId());
        
        if (rows == 0) {
            throw new IllegalStateException("表单定义不存在或已删除: " + formDefinition.getId());
        }
        
        log.info("表单定义更新成功: formId={}", formDefinition.getId());
    }

    @Override
    public void deleteFormDefinition(Long formId) {
        log.info("删除表单定义: formId={}", formId);
        
        String sql = "UPDATE wf_form_definition SET deleted = 1 WHERE id = ?";
        int rows = jdbcTemplate.update(sql, formId);
        
        if (rows == 0) {
            throw new IllegalStateException("表单定义不存在: " + formId);
        }
        
        log.info("表单定义删除成功: formId={}", formId);
    }

    @Override
    public FormDefinition getFormDefinitionById(Long formId) {
        log.info("根据ID查询表单定义: formId={}", formId);
        
        String sql = "SELECT * FROM wf_form_definition WHERE id = ? AND deleted = 0";
        
        try {
            List<FormDefinition> results = jdbcTemplate.query(sql, 
                    new BeanPropertyRowMapper<>(FormDefinition.class), formId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单定义失败: formId={}, error={}", formId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FormDefinition getFormDefinitionByKey(String formKey) {
        log.info("根据FormKey查询表单定义（最新版本）: formKey={}", formKey);
        
        String sql = "SELECT * FROM wf_form_definition WHERE form_key = ? AND deleted = 0 " +
                     "ORDER BY version DESC LIMIT 1";
        
        try {
            List<FormDefinition> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormDefinition.class), formKey);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单定义失败: formKey={}, error={}", formKey, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FormDefinition getFormDefinitionByKeyAndVersion(String formKey, Integer version) {
        log.info("根据FormKey和版本查询表单定义: formKey={}, version={}", formKey, version);
        
        String sql = "SELECT * FROM wf_form_definition WHERE form_key = ? AND version = ? AND deleted = 0";
        
        try {
            List<FormDefinition> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormDefinition.class), formKey, version);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单定义失败: formKey={}, version={}, error={}", formKey, version, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FormDefinition> listAllFormDefinitions() {
        log.info("查询所有表单定义");
        
        String sql = "SELECT * FROM wf_form_definition WHERE deleted = 0 ORDER BY create_time DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormDefinition.class));
    }

    @Override
    public List<FormDefinition> listFormDefinitionsByCategory(Long categoryId) {
        log.info("根据分类查询表单定义: categoryId={}", categoryId);
        
        String sql = "SELECT * FROM wf_form_definition WHERE category_id = ? AND deleted = 0 " +
                     "ORDER BY create_time DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormDefinition.class), categoryId);
    }

    @Override
    public List<FormDefinition> listFormDefinitionsByStatus(Integer status) {
        log.info("根据状态查询表单定义: status={}", status);
        
        String sql = "SELECT * FROM wf_form_definition WHERE status = ? AND deleted = 0 " +
                     "ORDER BY create_time DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormDefinition.class), status);
    }

    @Override
    public List<FormDefinition> listFormVersions(String formKey) {
        log.info("查询表单的所有版本: formKey={}", formKey);
        
        String sql = "SELECT * FROM wf_form_definition WHERE form_key = ? AND deleted = 0 " +
                     "ORDER BY version DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormDefinition.class), formKey);
    }

    @Override
    public Integer publishFormVersion(String formKey) {
        log.info("发布表单新版本: formKey={}", formKey);
        
        // 查询当前最大版本号
        String maxVersionSql = "SELECT MAX(version) FROM wf_form_definition WHERE form_key = ? AND deleted = 0";
        Integer maxVersion = jdbcTemplate.queryForObject(maxVersionSql, Integer.class, formKey);
        
        Integer newVersion = (maxVersion != null ? maxVersion : 0) + 1;
        
        log.info("新版本号: formKey={}, newVersion={}", formKey, newVersion);
        return newVersion;
    }
}

