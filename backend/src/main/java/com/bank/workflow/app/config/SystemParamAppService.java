package com.bank.workflow.app.config;

import com.bank.workflow.domain.config.entity.SystemParam;
import com.bank.workflow.domain.config.gateway.SystemParamGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统参数应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemParamAppService {

    private final SystemParamGateway systemParamGateway;

    /**
     * 根据ID查询参数
     */
    public SystemParam getById(Long id) {
        log.info("查询系统参数，ID: {}", id);
        return systemParamGateway.getById(id);
    }

    /**
     * 根据参数键查询
     */
    public SystemParam getByKey(String paramKey) {
        log.info("根据参数键查询，paramKey: {}", paramKey);
        return systemParamGateway.getByKey(paramKey);
    }

    /**
     * 查询所有参数
     */
    public List<SystemParam> listAll() {
        log.info("查询所有系统参数");
        return systemParamGateway.listAll();
    }

    /**
     * 根据分组查询参数
     */
    public List<SystemParam> listByGroup(String paramGroup) {
        log.info("根据分组查询系统参数，paramGroup: {}", paramGroup);
        return systemParamGateway.listByGroup(paramGroup);
    }

    /**
     * 创建参数
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean create(SystemParam param) {
        log.info("创建系统参数，paramKey: {}", param.getParamKey());

        // 检查参数键是否已存在
        SystemParam existing = systemParamGateway.getByKey(param.getParamKey());
        if (existing != null) {
            throw new IllegalArgumentException("参数键已存在: " + param.getParamKey());
        }

        param.setCreatedTime(LocalDateTime.now());
        return systemParamGateway.save(param);
    }

    /**
     * 更新参数
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SystemParam param) {
        log.info("更新系统参数，ID: {}, paramKey: {}", param.getId(), param.getParamKey());

        // 检查参数是否存在
        SystemParam existing = systemParamGateway.getById(param.getId());
        if (existing == null) {
            throw new IllegalArgumentException("参数不存在: " + param.getId());
        }

        param.setUpdatedTime(LocalDateTime.now());
        return systemParamGateway.update(param);
    }

    /**
     * 删除参数
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        log.info("删除系统参数，ID: {}", id);

        // 检查参数是否存在
        SystemParam existing = systemParamGateway.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("参数不存在: " + id);
        }

        // 系统参数不可删除
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new IllegalArgumentException("系统参数不可删除");
        }

        return systemParamGateway.deleteById(id);
    }

    /**
     * 刷新参数缓存
     */
    @CacheEvict(value = "sys:param", allEntries = true)
    public void refreshCache() {
        log.info("刷新系统参数缓存");
    }
}

