package org.atdev.artrip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "withdrawExecutor")
    public Executor withdrawExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);

        executor.initialize();
        return executor;
    }

    @Bean(name = "outboxExecutor")
    public Executor outboxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OutboxExecutor-");
        // 큐가 가득 차도 요청 스레드에 예외를 던지지 않는다. 버려진 건은 PENDING으로 남아 폴러가 처리한다
        executor.setRejectedExecutionHandler((task, pool) ->
                log.warn("알림 즉시 발송 큐 포화 - outbox 폴러가 재처리합니다"));
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (e, method, params) ->
                log.error("비동기 작업 실패 {}.{}", method.getDeclaringClass().getSimpleName(), method.getName(), e);
    }
}
