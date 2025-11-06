package com.bank.workflow.infrastructure.gateway;

import com.bank.workflow.domain.home.entity.HomeStatistics;
import com.bank.workflow.domain.home.gateway.HomeGateway;
import com.bank.workflow.infrastructure.persistence.mapper.DepartmentMapper;
import com.bank.workflow.infrastructure.persistence.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Component;

/**
 * 首页Gateway实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HomeGatewayImpl implements HomeGateway {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    @Override
    public HomeStatistics getHomeStatistics() {
        log.info("开始获取首页统计数据");

        HomeStatistics statistics = new HomeStatistics();

        try {
            // 1. 流程定义数量（活动的）
            long processCount = repositoryService.createProcessDefinitionQuery()
                    .active()
                    .count();
            statistics.setProcessCount(processCount);
            log.debug("流程定义数量: {}", processCount);

            // 2. 运行中流程数量
            long runningCount = runtimeService.createProcessInstanceQuery()
                    .active()
                    .count();
            statistics.setRunningCount(runningCount);
            log.debug("运行中流程数量: {}", runningCount);

            // 3. 待办任务数量（所有用户的待办任务）
            long todoCount = taskService.createTaskQuery()
                    .active()
                    .count();
            statistics.setTodoCount(todoCount);
            log.debug("待办任务数量: {}", todoCount);

            // 4. 已办任务数量（历史任务中已完成的）
            long doneCount = historyService.createHistoricTaskInstanceQuery()
                    .finished()
                    .count();
            statistics.setDoneCount(doneCount);
            log.debug("已办任务数量: {}", doneCount);

            // 5. 用户总数
            long userCount = userMapper.selectCount(new QueryWrapper<>());
            statistics.setUserCount(userCount);
            log.debug("用户总数: {}", userCount);

            // 6. 部门总数
            long departmentCount = departmentMapper.selectCount(new QueryWrapper<>());
            statistics.setDepartmentCount(departmentCount);
            log.debug("部门总数: {}", departmentCount);

            log.info("首页统计数据获取成功: {}", statistics);
            return statistics;

        } catch (Exception e) {
            log.error("获取首页统计数据失败", e);
            // 返回默认值
            statistics.setProcessCount(0L);
            statistics.setTodoCount(0L);
            statistics.setDoneCount(0L);
            statistics.setRunningCount(0L);
            statistics.setUserCount(0L);
            statistics.setDepartmentCount(0L);
            return statistics;
        }
    }
}

