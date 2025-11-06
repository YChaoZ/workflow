package com.bank.workflow.infrastructure.persistence.mapper;

import com.bank.workflow.infrastructure.persistence.po.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}

