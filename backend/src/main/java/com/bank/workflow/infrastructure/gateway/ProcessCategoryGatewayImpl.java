package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.category.entity.ProcessCategory;
import com.bank.workflow.domain.category.gateway.ProcessCategoryGateway;
import com.bank.workflow.infrastructure.persistence.mapper.ProcessCategoryMapper;
import com.bank.workflow.infrastructure.persistence.po.ProcessCategoryDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程分类网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessCategoryGatewayImpl implements ProcessCategoryGateway {
    
    private final ProcessCategoryMapper processCategoryMapper;
    
    @Override
    public Long createCategory(ProcessCategory category) {
        log.info("创建流程分类: category={}", category);
        
        ProcessCategoryDO categoryDO = convertToDO(category);
        categoryDO.setCreateTime(new Date());
        categoryDO.setUpdateTime(new Date());
        
        processCategoryMapper.insert(categoryDO);
        
        log.info("流程分类创建成功: categoryId={}", categoryDO.getId());
        return categoryDO.getId();
    }
    
    @Override
    public void updateCategory(ProcessCategory category) {
        log.info("更新流程分类: category={}", category);
        
        ProcessCategoryDO categoryDO = convertToDO(category);
        categoryDO.setUpdateTime(new Date());
        
        processCategoryMapper.updateById(categoryDO);
        
        log.info("流程分类更新成功: categoryId={}", category.getId());
    }
    
    @Override
    public void deleteCategory(Long categoryId) {
        log.info("删除流程分类: categoryId={}", categoryId);
        
        processCategoryMapper.deleteById(categoryId);
        
        log.info("流程分类删除成功: categoryId={}", categoryId);
    }
    
    @Override
    public ProcessCategory getCategoryById(Long categoryId) {
        log.info("查询流程分类详情: categoryId={}", categoryId);
        
        ProcessCategoryDO categoryDO = processCategoryMapper.selectById(categoryId);
        if (categoryDO == null) {
            log.warn("流程分类不存在: categoryId={}", categoryId);
            return null;
        }
        
        return convertToEntity(categoryDO);
    }
    
    @Override
    public ProcessCategory getCategoryByCode(String code) {
        log.info("根据编码查询流程分类: code={}", code);
        
        LambdaQueryWrapper<ProcessCategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessCategoryDO::getCategoryCode, code);
        
        ProcessCategoryDO categoryDO = processCategoryMapper.selectOne(wrapper);
        if (categoryDO == null) {
            log.warn("流程分类不存在: code={}", code);
            return null;
        }
        
        return convertToEntity(categoryDO);
    }
    
    @Override
    public List<ProcessCategory> listAllCategories() {
        log.info("查询所有流程分类");
        
        LambdaQueryWrapper<ProcessCategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ProcessCategoryDO::getSortOrder, ProcessCategoryDO::getId);
        
        List<ProcessCategoryDO> categoryDOList = processCategoryMapper.selectList(wrapper);
        List<ProcessCategory> result = new ArrayList<>();
        for (ProcessCategoryDO categoryDO : categoryDOList) {
            result.add(convertToEntity(categoryDO));
        }
        
        log.info("查询到{}个流程分类", result.size());
        return result;
    }
    
    @Override
    public List<ProcessCategory> listChildrenCategories(Long parentId) {
        log.info("查询子分类: parentId={}", parentId);
        
        LambdaQueryWrapper<ProcessCategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessCategoryDO::getParentId, parentId);
        wrapper.orderByAsc(ProcessCategoryDO::getSortOrder, ProcessCategoryDO::getId);
        
        List<ProcessCategoryDO> categoryDOList = processCategoryMapper.selectList(wrapper);
        List<ProcessCategory> result = new ArrayList<>();
        for (ProcessCategoryDO categoryDO : categoryDOList) {
            result.add(convertToEntity(categoryDO));
        }
        
        log.info("查询到{}个子分类", result.size());
        return result;
    }
    
    @Override
    public Long countChildrenCategories(Long parentId) {
        log.info("统计子分类数量: parentId={}", parentId);
        
        LambdaQueryWrapper<ProcessCategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessCategoryDO::getParentId, parentId);
        
        Long count = processCategoryMapper.selectCount(wrapper);
        
        log.info("子分类数量: {}", count);
        return count;
    }
    
    /**
     * 转换DO为领域实体
     */
    private ProcessCategory convertToEntity(ProcessCategoryDO categoryDO) {
        ProcessCategory category = new ProcessCategory();
        category.setId(categoryDO.getId());
        category.setName(categoryDO.getCategoryName());
        category.setCode(categoryDO.getCategoryCode());
        category.setParentId(categoryDO.getParentId());
        category.setSortOrder(categoryDO.getSortOrder());
        category.setCreatedTime(categoryDO.getCreateTime());
        category.setUpdatedTime(categoryDO.getUpdateTime());
        return category;
    }
    
    /**
     * 转换领域实体为DO
     */
    private ProcessCategoryDO convertToDO(ProcessCategory category) {
        ProcessCategoryDO categoryDO = new ProcessCategoryDO();
        categoryDO.setId(category.getId());
        categoryDO.setCategoryName(category.getName());
        categoryDO.setCategoryCode(category.getCode());
        categoryDO.setParentId(category.getParentId());
        categoryDO.setSortOrder(category.getSortOrder());
        return categoryDO;
    }
}

