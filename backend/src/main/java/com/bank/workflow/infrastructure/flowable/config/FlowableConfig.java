package com.bank.workflow.infrastructure.flowable.config;

import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 引擎配置
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        // 异步执行器配置
        engineConfiguration.setAsyncExecutorActivate(true);
        engineConfiguration.setAsyncExecutorCorePoolSize(5);
        engineConfiguration.setAsyncExecutorMaxPoolSize(10);
        
        // 历史记录级别 - 使用 full（记录所有历史信息）
        engineConfiguration.setHistoryLevel(HistoryLevel.FULL);
        
        // 流程定义缓存
        engineConfiguration.setProcessDefinitionCacheLimit(10);
        
        // 字体配置（解决流程图中文乱码）
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
    }
}

