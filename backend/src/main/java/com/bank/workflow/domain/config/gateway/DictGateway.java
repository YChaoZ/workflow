package com.bank.workflow.domain.config.gateway;

import com.bank.workflow.domain.config.entity.DictData;
import com.bank.workflow.domain.config.entity.DictType;

import java.util.List;

/**
 * 数据字典Gateway
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface DictGateway {

    // ==================== 字典类型 ====================

    /**
     * 根据ID查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型实体
     */
    DictType getDictTypeById(Long id);

    /**
     * 根据编码查询字典类型
     *
     * @param dictCode 字典编码
     * @return 字典类型实体
     */
    DictType getDictTypeByCode(String dictCode);

    /**
     * 查询所有字典类型
     *
     * @return 字典类型列表
     */
    List<DictType> listAllDictTypes();

    /**
     * 保存字典类型
     *
     * @param dictType 字典类型实体
     * @return 是否成功
     */
    boolean saveDictType(DictType dictType);

    /**
     * 更新字典类型
     *
     * @param dictType 字典类型实体
     * @return 是否成功
     */
    boolean updateDictType(DictType dictType);

    /**
     * 删除字典类型
     *
     * @param id 字典类型ID
     * @return 是否成功
     */
    boolean deleteDictTypeById(Long id);

    // ==================== 字典数据 ====================

    /**
     * 根据ID查询字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据实体
     */
    DictData getDictDataById(Long id);

    /**
     * 根据字典类型编码查询字典数据
     *
     * @param dictCode 字典类型编码
     * @return 字典数据列表
     */
    List<DictData> listDictDataByTypeCode(String dictCode);

    /**
     * 根据字典类型ID查询字典数据
     *
     * @param dictTypeId 字典类型ID
     * @return 字典数据列表
     */
    List<DictData> listDictDataByTypeId(Long dictTypeId);

    /**
     * 保存字典数据
     *
     * @param dictData 字典数据实体
     * @return 是否成功
     */
    boolean saveDictData(DictData dictData);

    /**
     * 更新字典数据
     *
     * @param dictData 字典数据实体
     * @return 是否成功
     */
    boolean updateDictData(DictData dictData);

    /**
     * 删除字典数据
     *
     * @param id 字典数据ID
     * @return 是否成功
     */
    boolean deleteDictDataById(Long id);
}

