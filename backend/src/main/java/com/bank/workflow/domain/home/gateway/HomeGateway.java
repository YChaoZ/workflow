package com.bank.workflow.domain.home.gateway;

import com.bank.workflow.domain.home.entity.HomeStatistics;

/**
 * 首页Gateway接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface HomeGateway {

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    HomeStatistics getHomeStatistics();
}

