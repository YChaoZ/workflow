package com.bank.workflow.adapter.web;

import com.bank.workflow.app.home.HomeAppService;
import com.bank.workflow.domain.home.entity.HomeStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页Controller
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeAppService homeAppService;

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Map<String, Object> getHomeStatistics() {
        log.info("接收到获取首页统计数据请求");

        Map<String, Object> response = new HashMap<>();
        try {
            HomeStatistics statistics = homeAppService.getHomeStatistics();
            response.put("code", 200);
            response.put("data", statistics);
            response.put("message", "获取成功");
            log.info("首页统计数据返回成功");
        } catch (Exception e) {
            log.error("获取首页统计数据失败", e);
            response.put("code", 500);
            response.put("message", "获取失败: " + e.getMessage());
        }

        return response;
    }
}

