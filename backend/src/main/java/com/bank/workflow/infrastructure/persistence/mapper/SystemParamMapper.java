package com.bank.workflow.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.workflow.domain.config.entity.SystemParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统参数Mapper
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Mapper
public interface SystemParamMapper extends BaseMapper<SystemParam> {
}

