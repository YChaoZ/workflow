package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.config.entity.DictData;
import com.bank.workflow.domain.config.entity.DictType;
import com.bank.workflow.domain.config.gateway.DictGateway;
import com.bank.workflow.infrastructure.persistence.mapper.DictDataMapper;
import com.bank.workflow.infrastructure.persistence.mapper.DictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据字典Gateway实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DictGatewayImpl implements DictGateway {

    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;

    // ==================== 字典类型 ====================

    @Override
    public DictType getDictTypeById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public DictType getDictTypeByCode(String dictCode) {
        return dictTypeMapper.selectOne(
                new LambdaQueryWrapper<DictType>()
                        .eq(DictType::getDictCode, dictCode)
        );
    }

    @Override
    public List<DictType> listAllDictTypes() {
        return dictTypeMapper.selectList(
                new LambdaQueryWrapper<DictType>()
                        .orderByAsc(DictType::getSortOrder)
        );
    }

    @Override
    public boolean saveDictType(DictType dictType) {
        return dictTypeMapper.insert(dictType) > 0;
    }

    @Override
    @CacheEvict(value = "sys:dict", key = "#dictType.dictCode")
    public boolean updateDictType(DictType dictType) {
        return dictTypeMapper.updateById(dictType) > 0;
    }

    @Override
    @CacheEvict(value = "sys:dict", allEntries = true)
    public boolean deleteDictTypeById(Long id) {
        // 删除字典类型时，级联删除字典数据（已在数据库外键设置）
        return dictTypeMapper.deleteById(id) > 0;
    }

    // ==================== 字典数据 ====================

    @Override
    public DictData getDictDataById(Long id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "sys:dict", key = "#dictCode", unless = "#result == null || #result.isEmpty()")
    public List<DictData> listDictDataByTypeCode(String dictCode) {
        return dictDataMapper.selectList(
                new LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDictCode, dictCode)
                        .eq(DictData::getStatus, true)
                        .orderByAsc(DictData::getSortOrder)
        );
    }

    @Override
    public List<DictData> listDictDataByTypeId(Long dictTypeId) {
        return dictDataMapper.selectList(
                new LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDictTypeId, dictTypeId)
                        .orderByAsc(DictData::getSortOrder)
        );
    }

    @Override
    @CacheEvict(value = "sys:dict", key = "#dictData.dictCode")
    public boolean saveDictData(DictData dictData) {
        return dictDataMapper.insert(dictData) > 0;
    }

    @Override
    @CacheEvict(value = "sys:dict", key = "#dictData.dictCode")
    public boolean updateDictData(DictData dictData) {
        return dictDataMapper.updateById(dictData) > 0;
    }

    @Override
    @CacheEvict(value = "sys:dict", allEntries = true)
    public boolean deleteDictDataById(Long id) {
        return dictDataMapper.deleteById(id) > 0;
    }
}

