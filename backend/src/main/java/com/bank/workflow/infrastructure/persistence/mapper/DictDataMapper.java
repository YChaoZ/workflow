package com.bank.workflow.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.workflow.domain.config.entity.DictData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典数据Mapper
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {
}

