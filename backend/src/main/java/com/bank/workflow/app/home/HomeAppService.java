package com.bank.workflow.app.home;

import com.bank.workflow.domain.home.entity.HomeStatistics;
import com.bank.workflow.domain.home.gateway.HomeGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 首页应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeAppService {

    private final HomeGateway homeGateway;

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    public HomeStatistics getHomeStatistics() {
        log.info("获取首页统计数据");
        return homeGateway.getHomeStatistics();
    }
}

