package com.bank.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 工作流系统启动类
 * 
 * @author Workflow Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.bank.workflow")
public class WorkFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkFlowApplication.class, args);
        System.out.println("""
            
            ============================================
            工作流系统启动成功！
            Workflow System Started Successfully!
            ============================================
            访问地址: http://localhost:9099
            接口文档: http://localhost:9099/doc.html
            Druid监控: http://localhost:9099/druid/
            ============================================
            """);
    }
}

