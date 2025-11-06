package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.form.entity.FormData;
import com.bank.workflow.domain.form.gateway.FormDataGateway;
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
 * 表单数据网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FormDataGatewayImpl implements FormDataGateway {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long saveFormData(FormData formData) {
        log.info("保存表单数据: formId={}, processInstanceId={}", 
                formData.getFormId(), formData.getProcessInstanceId());
        
        String sql = "INSERT INTO wf_form_data (form_id, process_instance_id, task_id, " +
                     "form_data, create_by, submit_user) VALUES (?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, formData.getFormId());
            ps.setString(2, formData.getProcessInstanceId());
            ps.setString(3, formData.getTaskId());
            ps.setString(4, formData.getFormData());
            ps.setString(5, formData.getCreateBy());
            ps.setString(6, formData.getSubmitUser());
            return ps;
        }, keyHolder);
        
        Long dataId = keyHolder.getKey().longValue();
        log.info("表单数据保存成功: dataId={}", dataId);
        return dataId;
    }

    @Override
    public void updateFormData(FormData formData) {
        log.info("更新表单数据: dataId={}", formData.getId());
        
        String sql = "UPDATE wf_form_data SET form_data = ?, submit_user = ? WHERE id = ? AND deleted = 0";
        
        int rows = jdbcTemplate.update(sql,
                formData.getFormData(),
                formData.getSubmitUser(),
                formData.getId());
        
        if (rows == 0) {
            throw new IllegalStateException("表单数据不存在或已删除: " + formData.getId());
        }
        
        log.info("表单数据更新成功: dataId={}", formData.getId());
    }

    @Override
    public FormData getFormDataById(Long dataId) {
        log.info("根据ID查询表单数据: dataId={}", dataId);
        
        String sql = "SELECT * FROM wf_form_data WHERE id = ? AND deleted = 0";
        
        try {
            List<FormData> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormData.class), dataId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单数据失败: dataId={}, error={}", dataId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FormData getFormDataByProcessInstanceId(String processInstanceId) {
        log.info("根据流程实例ID查询表单数据: processInstanceId={}", processInstanceId);
        
        String sql = "SELECT * FROM wf_form_data WHERE process_instance_id = ? AND deleted = 0 " +
                     "ORDER BY submit_time DESC LIMIT 1";
        
        try {
            List<FormData> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormData.class), processInstanceId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单数据失败: processInstanceId={}, error={}", 
                    processInstanceId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public FormData getFormDataByTaskId(String taskId) {
        log.info("根据任务ID查询表单数据: taskId={}", taskId);
        
        String sql = "SELECT * FROM wf_form_data WHERE task_id = ? AND deleted = 0 LIMIT 1";
        
        try {
            List<FormData> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormData.class), taskId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单数据失败: taskId={}, error={}", taskId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FormData> listFormDataByFormId(Long formId) {
        log.info("根据表单ID查询所有数据: formId={}", formId);
        
        String sql = "SELECT * FROM wf_form_data WHERE form_id = ? AND deleted = 0 " +
                     "ORDER BY submit_time DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormData.class), formId);
    }

    @Override
    public void deleteFormData(Long dataId) {
        log.info("删除表单数据: dataId={}", dataId);
        
        String sql = "UPDATE wf_form_data SET deleted = 1 WHERE id = ?";
        int rows = jdbcTemplate.update(sql, dataId);
        
        if (rows == 0) {
            throw new IllegalStateException("表单数据不存在: " + dataId);
        }
        
        log.info("表单数据删除成功: dataId={}", dataId);
    }
}

