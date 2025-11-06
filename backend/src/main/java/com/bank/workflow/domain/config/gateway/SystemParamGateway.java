package com.bank.workflow.domain.config.gateway;

import com.bank.workflow.domain.config.entity.SystemParam;

import java.util.List;

/**
 * 系统参数Gateway
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface SystemParamGateway {

    /**
     * 根据ID查询参数
     *
     * @param id 参数ID
     * @return 参数实体
     */
    SystemParam getById(Long id);

    /**
     * 根据参数键查询
     *
     * @param paramKey 参数键
     * @return 参数实体
     */
    SystemParam getByKey(String paramKey);

    /**
     * 查询所有参数
     *
     * @return 参数列表
     */
    List<SystemParam> listAll();

    /**
     * 根据分组查询参数
     *
     * @param paramGroup 参数分组
     * @return 参数列表
     */
    List<SystemParam> listByGroup(String paramGroup);

    /**
     * 保存参数
     *
     * @param param 参数实体
     * @return 是否成功
     */
    boolean save(SystemParam param);

    /**
     * 更新参数
     *
     * @param param 参数实体
     * @return 是否成功
     */
    boolean update(SystemParam param);

    /**
     * 删除参数
     *
     * @param id 参数ID
     * @return 是否成功
     */
    boolean deleteById(Long id);
}

