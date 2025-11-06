package com.bank.workflow.app.config;

import com.bank.workflow.domain.config.entity.DictData;
import com.bank.workflow.domain.config.entity.DictType;
import com.bank.workflow.domain.config.gateway.DictGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据字典应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictAppService {

    private final DictGateway dictGateway;

    // ==================== 字典类型 ====================

    /**
     * 根据ID查询字典类型
     */
    public DictType getDictTypeById(Long id) {
        log.info("查询字典类型，ID: {}", id);
        return dictGateway.getDictTypeById(id);
    }

    /**
     * 根据编码查询字典类型
     */
    public DictType getDictTypeByCode(String dictCode) {
        log.info("根据编码查询字典类型，dictCode: {}", dictCode);
        return dictGateway.getDictTypeByCode(dictCode);
    }

    /**
     * 查询所有字典类型
     */
    public List<DictType> listAllDictTypes() {
        log.info("查询所有字典类型");
        return dictGateway.listAllDictTypes();
    }

    /**
     * 创建字典类型
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictType(DictType dictType) {
        log.info("创建字典类型，dictCode: {}", dictType.getDictCode());

        // 检查字典编码是否已存在
        DictType existing = dictGateway.getDictTypeByCode(dictType.getDictCode());
        if (existing != null) {
            throw new IllegalArgumentException("字典编码已存在: " + dictType.getDictCode());
        }

        dictType.setCreatedTime(LocalDateTime.now());
        return dictGateway.saveDictType(dictType);
    }

    /**
     * 更新字典类型
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(DictType dictType) {
        log.info("更新字典类型，ID: {}, dictCode: {}", dictType.getId(), dictType.getDictCode());

        // 检查字典类型是否存在
        DictType existing = dictGateway.getDictTypeById(dictType.getId());
        if (existing == null) {
            throw new IllegalArgumentException("字典类型不存在: " + dictType.getId());
        }

        dictType.setUpdatedTime(LocalDateTime.now());
        return dictGateway.updateDictType(dictType);
    }

    /**
     * 删除字典类型
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(Long id) {
        log.info("删除字典类型，ID: {}", id);

        // 检查字典类型是否存在
        DictType existing = dictGateway.getDictTypeById(id);
        if (existing == null) {
            throw new IllegalArgumentException("字典类型不存在: " + id);
        }

        // 系统字典不可删除
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new IllegalArgumentException("系统字典不可删除");
        }

        return dictGateway.deleteDictTypeById(id);
    }

    // ==================== 字典数据 ====================

    /**
     * 根据ID查询字典数据
     */
    public DictData getDictDataById(Long id) {
        log.info("查询字典数据，ID: {}", id);
        return dictGateway.getDictDataById(id);
    }

    /**
     * 根据字典类型编码查询字典数据
     */
    public List<DictData> listDictDataByTypeCode(String dictCode) {
        log.info("根据字典类型编码查询字典数据，dictCode: {}", dictCode);
        return dictGateway.listDictDataByTypeCode(dictCode);
    }

    /**
     * 根据字典类型ID查询字典数据
     */
    public List<DictData> listDictDataByTypeId(Long dictTypeId) {
        log.info("根据字典类型ID查询字典数据，dictTypeId: {}", dictTypeId);
        return dictGateway.listDictDataByTypeId(dictTypeId);
    }

    /**
     * 创建字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictData(DictData dictData) {
        log.info("创建字典数据，dictCode: {}, dictLabel: {}", dictData.getDictCode(), dictData.getDictLabel());

        // 检查字典类型是否存在
        if (dictData.getDictTypeId() == null && dictData.getDictCode() != null) {
            DictType dictType = dictGateway.getDictTypeByCode(dictData.getDictCode());
            if (dictType != null) {
                dictData.setDictTypeId(dictType.getId());
            }
        }

        dictData.setCreatedTime(LocalDateTime.now());
        return dictGateway.saveDictData(dictData);
    }

    /**
     * 更新字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(DictData dictData) {
        log.info("更新字典数据，ID: {}", dictData.getId());

        // 检查字典数据是否存在
        DictData existing = dictGateway.getDictDataById(dictData.getId());
        if (existing == null) {
            throw new IllegalArgumentException("字典数据不存在: " + dictData.getId());
        }

        dictData.setUpdatedTime(LocalDateTime.now());
        return dictGateway.updateDictData(dictData);
    }

    /**
     * 删除字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictData(Long id) {
        log.info("删除字典数据，ID: {}", id);

        // 检查字典数据是否存在
        DictData existing = dictGateway.getDictDataById(id);
        if (existing == null) {
            throw new IllegalArgumentException("字典数据不存在: " + id);
        }

        return dictGateway.deleteDictDataById(id);
    }

    /**
     * 刷新字典缓存
     */
    @CacheEvict(value = "sys:dict", allEntries = true)
    public void refreshCache() {
        log.info("刷新数据字典缓存");
    }
}

