package com.dqgs.monitor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 配置定时任务线程数量,可以添加@EnableAsync注解实现异步任务
 *
 * @author by Passer
 * @version 1.0
 * @date 2020/8/3 16:45
 */
@EnableAsync
@Configuration
@Slf4j
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        //设置线程池
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("Schedule-Task-");
        taskScheduler.setPoolSize(20);
        taskScheduler.initialize();
        taskScheduler.setAwaitTerminationSeconds(60);
        taskScheduler.setErrorHandler(throwable -> log.error("调度任务发生异常", throwable));
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        return taskScheduler;
    }
}
