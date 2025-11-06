package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.form.entity.ProcessForm;
import com.bank.workflow.domain.form.gateway.ProcessFormGateway;
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
 * 流程表单关联网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessFormGatewayImpl implements ProcessFormGateway {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createProcessForm(ProcessForm processForm) {
        log.info("创建流程表单关联: processKey={}, nodeId={}, formKey={}", 
                processForm.getProcessDefinitionKey(), processForm.getNodeId(), processForm.getFormKey());
        
        String sql = "INSERT INTO wf_process_form (process_definition_key, node_id, form_key, " +
                     "form_version, is_required) VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, processForm.getProcessDefinitionKey());
            ps.setString(2, processForm.getNodeId());
            ps.setString(3, processForm.getFormKey());
            ps.setInt(4, processForm.getFormVersion() != null ? processForm.getFormVersion() : 1);
            ps.setInt(5, processForm.getIsRequired() != null ? processForm.getIsRequired() : 1);
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey().longValue();
        log.info("流程表单关联创建成功: id={}", id);
        return id;
    }

    @Override
    public void updateProcessForm(ProcessForm processForm) {
        log.info("更新流程表单关联: id={}", processForm.getId());
        
        String sql = "UPDATE wf_process_form SET form_key = ?, form_version = ?, is_required = ? WHERE id = ?";
        
        int rows = jdbcTemplate.update(sql,
                processForm.getFormKey(),
                processForm.getFormVersion(),
                processForm.getIsRequired(),
                processForm.getId());
        
        if (rows == 0) {
            throw new IllegalStateException("流程表单关联不存在: " + processForm.getId());
        }
        
        log.info("流程表单关联更新成功: id={}", processForm.getId());
    }

    @Override
    public void deleteProcessForm(Long id) {
        log.info("删除流程表单关联: id={}", id);
        
        String sql = "DELETE FROM wf_process_form WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        
        if (rows == 0) {
            throw new IllegalStateException("流程表单关联不存在: " + id);
        }
        
        log.info("流程表单关联删除成功: id={}", id);
    }

    @Override
    public ProcessForm getProcessFormById(Long id) {
        log.info("根据ID查询流程表单关联: id={}", id);
        
        String sql = "SELECT * FROM wf_process_form WHERE id = ?";
        
        try {
            List<ProcessForm> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(ProcessForm.class), id);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询流程表单关联失败: id={}, error={}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<ProcessForm> listProcessFormsByProcessKey(String processDefinitionKey) {
        log.info("根据流程定义Key查询所有表单关联: processKey={}", processDefinitionKey);
        
        String sql = "SELECT * FROM wf_process_form WHERE process_definition_key = ? ORDER BY id";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ProcessForm.class), processDefinitionKey);
    }

    @Override
    public ProcessForm getProcessFormByProcessKeyAndNodeId(String processDefinitionKey, String nodeId) {
        log.info("根据流程定义Key和节点ID查询表单关联: processKey={}, nodeId={}", 
                processDefinitionKey, nodeId);
        
        String sql = "SELECT * FROM wf_process_form WHERE process_definition_key = ? AND node_id = ? LIMIT 1";
        
        try {
            List<ProcessForm> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(ProcessForm.class), processDefinitionKey, nodeId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询流程表单关联失败: processKey={}, nodeId={}, error={}", 
                    processDefinitionKey, nodeId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ProcessForm getGlobalFormByProcessKey(String processDefinitionKey) {
        log.info("查询流程的全局表单: processKey={}", processDefinitionKey);
        
        String sql = "SELECT * FROM wf_process_form WHERE process_definition_key = ? " +
                     "AND (node_id IS NULL OR node_id = '') LIMIT 1";
        
        try {
            List<ProcessForm> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(ProcessForm.class), processDefinitionKey);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询流程全局表单失败: processKey={}, error={}", 
                    processDefinitionKey, e.getMessage(), e);
            return null;
        }
    }
}

