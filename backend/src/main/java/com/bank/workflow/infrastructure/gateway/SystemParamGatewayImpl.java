package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.config.entity.SystemParam;
import com.bank.workflow.domain.config.gateway.SystemParamGateway;
import com.bank.workflow.infrastructure.persistence.mapper.SystemParamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统参数Gateway实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class SystemParamGatewayImpl implements SystemParamGateway {

    private final SystemParamMapper systemParamMapper;

    @Override
    public SystemParam getById(Long id) {
        return systemParamMapper.selectById(id);
    }

    @Override
    @Cacheable(value = "sys:param", key = "#paramKey", unless = "#result == null")
    public SystemParam getByKey(String paramKey) {
        return systemParamMapper.selectOne(
                new LambdaQueryWrapper<SystemParam>()
                        .eq(SystemParam::getParamKey, paramKey)
        );
    }

    @Override
    public List<SystemParam> listAll() {
        return systemParamMapper.selectList(
                new LambdaQueryWrapper<SystemParam>()
                        .orderByAsc(SystemParam::getParamGroup, SystemParam::getSortOrder)
        );
    }

    @Override
    public List<SystemParam> listByGroup(String paramGroup) {
        return systemParamMapper.selectList(
                new LambdaQueryWrapper<SystemParam>()
                        .eq(SystemParam::getParamGroup, paramGroup)
                        .orderByAsc(SystemParam::getSortOrder)
        );
    }

    @Override
    public boolean save(SystemParam param) {
        return systemParamMapper.insert(param) > 0;
    }

    @Override
    @CacheEvict(value = "sys:param", key = "#param.paramKey")
    public boolean update(SystemParam param) {
        return systemParamMapper.updateById(param) > 0;
    }

    @Override
    @CacheEvict(value = "sys:param", allEntries = true)
    public boolean deleteById(Long id) {
        return systemParamMapper.deleteById(id) > 0;
    }
}

