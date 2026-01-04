package org.atdev.artrip.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RetryConfig {

    private final RetryRegistry retryRegistry;

    @Bean
    public Retry publicDataApiRetry() {
        Retry retry = retryRegistry.retry("publicDataApi");

        retry.getEventPublisher()
                .onRetry(event -> log.warn("공공데이터 API 재시도 - 시도 횟수 : {}, 원인 : {}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().getMessage()))
                .onError(event -> log.error("공공데이터 API 실패 - 시도 횟수 : {}",
                        event.getNumberOfRetryAttempts()))
                .onSuccess(event -> log.info("공공데이터 API 성공 - 시도 횟수 : {}",
                        event.getNumberOfRetryAttempts()));
        return retry;
    }
}
