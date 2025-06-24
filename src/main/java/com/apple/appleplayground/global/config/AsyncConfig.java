package com.apple.appleplayground.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 작업 설정
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 고성능 파일 업로드용 비동기 스레드 풀
     */
    @Bean(name = "fileUploadExecutor")
    public Executor fileUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int coreCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(coreCount);
        executor.setMaxPoolSize(coreCount * 3);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("S3Upload-");
        executor.setKeepAliveSeconds(120);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    /**
     * 고성능 일반 비동기 작업용 스레드 풀
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int coreCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(coreCount * 2);
        executor.setMaxPoolSize(coreCount * 4);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Async-");
        executor.setKeepAliveSeconds(180);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
