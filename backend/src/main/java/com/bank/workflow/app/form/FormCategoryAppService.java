package com.bank.workflow.app.form;

import com.bank.workflow.domain.form.entity.FormCategory;
import com.bank.workflow.domain.form.gateway.FormCategoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表单分类应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormCategoryAppService {

    private final FormCategoryGateway formCategoryGateway;

    /**
     * 创建表单分类
     *
     * @param category 表单分类
     * @return 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createFormCategory(FormCategory category) {
        log.info("接收创建表单分类请求: category={}", category);

        // 检查父分类是否存在
        if (category.getParentId() != null && category.getParentId() > 0) {
            FormCategory parent = formCategoryGateway.getFormCategoryById(category.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在: " + category.getParentId());
            }
        }

        Long categoryId = formCategoryGateway.createFormCategory(category);

        log.info("表单分类创建成功: categoryId={}", categoryId);
        return categoryId;
    }

    /**
     * 更新表单分类
     *
     * @param category 表单分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFormCategory(FormCategory category) {
        log.info("接收更新表单分类请求: category={}", category);

        // 检查分类是否存在
        FormCategory existing = formCategoryGateway.getFormCategoryById(category.getId());
        if (existing == null) {
            throw new IllegalArgumentException("表单分类不存在: " + category.getId());
        }

        // 检查父分类是否存在
        if (category.getParentId() != null && category.getParentId() > 0) {
            FormCategory parent = formCategoryGateway.getFormCategoryById(category.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在: " + category.getParentId());
            }
            
            // 不能将分类设置为自己的子分类
            if (category.getParentId().equals(category.getId())) {
                throw new IllegalArgumentException("不能将分类设置为自己的子分类");
            }
        }

        formCategoryGateway.updateFormCategory(category);

        log.info("表单分类更新成功: categoryId={}", category.getId());
    }

    /**
     * 删除表单分类
     *
     * @param categoryId 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormCategory(Long categoryId) {
        log.info("接收删除表单分类请求: categoryId={}", categoryId);

        // 检查分类是否存在
        FormCategory existing = formCategoryGateway.getFormCategoryById(categoryId);
        if (existing == null) {
            throw new IllegalArgumentException("表单分类不存在: " + categoryId);
        }

        // 检查是否有子分类
        long childrenCount = formCategoryGateway.countChildrenCategories(categoryId);
        if (childrenCount > 0) {
            throw new IllegalArgumentException("存在子分类，无法删除");
        }

        // TODO: 检查是否有表单使用该分类

        formCategoryGateway.deleteFormCategory(categoryId);

        log.info("表单分类删除成功: categoryId={}", categoryId);
    }

    /**
     * 根据ID查询表单分类
     *
     * @param categoryId 分类ID
     * @return 表单分类
     */
    public FormCategory getFormCategoryById(Long categoryId) {
        log.info("根据ID查询表单分类: categoryId={}", categoryId);
        return formCategoryGateway.getFormCategoryById(categoryId);
    }

    /**
     * 查询所有表单分类（树形结构）
     *
     * @return 表单分类树
     */
    public List<FormCategory> getFormCategoryTree() {
        log.info("查询表单分类树");

        // 查询所有分类
        List<FormCategory> allCategories = formCategoryGateway.listAllFormCategories();

        // 构建树形结构
        return buildCategoryTree(allCategories, 0L);
    }

    /**
     * 查询所有表单分类（列表）
     *
     * @return 表单分类列表
     */
    public List<FormCategory> listAllFormCategories() {
        log.info("查询所有表单分类");
        return formCategoryGateway.listAllFormCategories();
    }

    /**
     * 构建分类树
     *
     * @param categories 所有分类
     * @param parentId   父分类ID
     * @return 分类树
     */
    private List<FormCategory> buildCategoryTree(List<FormCategory> categories, Long parentId) {
        Map<Long, List<FormCategory>> categoryMap = categories.stream()
                .collect(Collectors.groupingBy(FormCategory::getParentId));

        List<FormCategory> tree = categoryMap.getOrDefault(parentId, new ArrayList<>());

        for (FormCategory category : tree) {
            List<FormCategory> children = buildCategoryTree(categories, category.getId());
            if (!children.isEmpty()) {
                category.setChildren(children);
            }
        }

        return tree;
    }
}

