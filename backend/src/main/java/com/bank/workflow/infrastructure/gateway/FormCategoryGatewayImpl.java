package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.form.entity.FormCategory;
import com.bank.workflow.domain.form.gateway.FormCategoryGateway;
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
 * 表单分类网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FormCategoryGatewayImpl implements FormCategoryGateway {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createFormCategory(FormCategory category) {
        log.info("创建表单分类: categoryCode={}", category.getCategoryCode());
        
        String sql = "INSERT INTO wf_form_category (category_code, category_name, parent_id, " +
                     "sort_order, description, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setLong(3, category.getParentId() != null ? category.getParentId() : 0);
            ps.setInt(4, category.getSortOrder() != null ? category.getSortOrder() : 0);
            ps.setString(5, category.getDescription());
            ps.setInt(6, category.getStatus() != null ? category.getStatus() : 1);
            return ps;
        }, keyHolder);
        
        Long categoryId = keyHolder.getKey().longValue();
        log.info("表单分类创建成功: categoryId={}", categoryId);
        return categoryId;
    }

    @Override
    public void updateFormCategory(FormCategory category) {
        log.info("更新表单分类: categoryId={}", category.getId());
        
        String sql = "UPDATE wf_form_category SET category_name = ?, parent_id = ?, " +
                     "sort_order = ?, description = ?, status = ? WHERE id = ? AND deleted = 0";
        
        int rows = jdbcTemplate.update(sql,
                category.getCategoryName(),
                category.getParentId(),
                category.getSortOrder(),
                category.getDescription(),
                category.getStatus(),
                category.getId());
        
        if (rows == 0) {
            throw new IllegalStateException("表单分类不存在或已删除: " + category.getId());
        }
        
        log.info("表单分类更新成功: categoryId={}", category.getId());
    }

    @Override
    public void deleteFormCategory(Long categoryId) {
        log.info("删除表单分类: categoryId={}", categoryId);
        
        String sql = "UPDATE wf_form_category SET deleted = 1 WHERE id = ?";
        int rows = jdbcTemplate.update(sql, categoryId);
        
        if (rows == 0) {
            throw new IllegalStateException("表单分类不存在: " + categoryId);
        }
        
        log.info("表单分类删除成功: categoryId={}", categoryId);
    }

    @Override
    public FormCategory getFormCategoryById(Long categoryId) {
        log.info("根据ID查询表单分类: categoryId={}", categoryId);
        
        String sql = "SELECT * FROM wf_form_category WHERE id = ? AND deleted = 0";
        
        try {
            List<FormCategory> results = jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(FormCategory.class), categoryId);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error("查询表单分类失败: categoryId={}, error={}", categoryId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FormCategory> listAllFormCategories() {
        log.info("查询所有表单分类");
        
        String sql = "SELECT * FROM wf_form_category WHERE deleted = 0 ORDER BY sort_order, id";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormCategory.class));
    }

    @Override
    public List<FormCategory> listFormCategoriesByParentId(Long parentId) {
        log.info("根据父分类ID查询子分类: parentId={}", parentId);
        
        String sql = "SELECT * FROM wf_form_category WHERE parent_id = ? AND deleted = 0 " +
                     "ORDER BY sort_order, id";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FormCategory.class), parentId);
    }

    @Override
    public long countChildrenCategories(Long categoryId) {
        log.info("统计子分类数量: categoryId={}", categoryId);
        
        String sql = "SELECT COUNT(*) FROM wf_form_category WHERE parent_id = ? AND deleted = 0";
        
        Long count = jdbcTemplate.queryForObject(sql, Long.class, categoryId);
        return count != null ? count : 0;
    }
}

