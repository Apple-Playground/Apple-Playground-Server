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
        
        // CPU 코어 수에 따른 동적 설정
        int coreCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(coreCount);                    // CPU 코어 수
        executor.setMaxPoolSize(coreCount * 3);                 // 최대 스레드 = 코어 수 * 3
        executor.setQueueCapacity(500);                         // 대기열 크기 증가
        
        executor.setThreadNamePrefix("S3Upload-");
        executor.setKeepAliveSeconds(120);                      // 유휴 스레드 생존 시간 증가
        executor.setAllowCoreThreadTimeOut(true);               // 코어 스레드 타임아웃 허용
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);                // 종료 대기 시간 증가
        
        // 거부 정책: 대기열이 가득 차면 호출자 스레드에서 직접 실행
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
        
        // CPU 코어 수에 따른 동적 설정
        int coreCount = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(coreCount * 2);                // I/O 작업이 많으므로 코어 수 * 2
        executor.setMaxPoolSize(coreCount * 4);                 // 최대 스레드 = 코어 수 * 4
        executor.setQueueCapacity(1000);                        // 대기열 크기 증가
        
        executor.setThreadNamePrefix("Async-");
        executor.setKeepAliveSeconds(180);                      // 유휴 스레드 생존 시간
        executor.setAllowCoreThreadTimeOut(true);               // 코어 스레드 타임아웃 허용
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // 거부 정책: 대기열이 가득 차면 호출자 스레드에서 직접 실행
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
}
