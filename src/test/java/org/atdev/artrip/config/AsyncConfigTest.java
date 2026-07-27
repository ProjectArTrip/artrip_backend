package org.atdev.artrip.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AsyncConfigTest {

    private final AsyncConfig asyncConfig = new AsyncConfig();

    @Test
    @DisplayName("withdrawExecutor는 ThreadPoolTaskExecutor로 생성되고 초기화되어 있다")
    void withdrawExecutor_isInitializedThreadPoolTaskExecutor() {
        Executor executor = asyncConfig.withdrawExecutor();

        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
        ThreadPoolTaskExecutor pool = (ThreadPoolTaskExecutor) executor;
        assertThat(pool.getThreadPoolExecutor()).isNotNull();
    }

    @Test
    @DisplayName("exhibitSyncExecutor는 단일 스레드(core=1, max=1)로 구성된다")
    void exhibitSyncExecutor_isSingleThreaded() {
        Executor executor = asyncConfig.exhibitSyncExecutor();

        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
        ThreadPoolTaskExecutor pool = (ThreadPoolTaskExecutor) executor;
        assertThat(pool.getCorePoolSize()).isEqualTo(1);
        assertThat(pool.getMaxPoolSize()).isEqualTo(1);
        assertThat(pool.getThreadNamePrefix()).isEqualTo("[exhibit-sync] ");
    }

    @Test
    @DisplayName("exhibitSyncExecutor는 매 호출마다 독립된 인스턴스를 반환한다")
    void exhibitSyncExecutor_returnsIndependentInstances() {
        Executor first = asyncConfig.exhibitSyncExecutor();
        Executor second = asyncConfig.exhibitSyncExecutor();

        assertThat(first).isNotSameAs(second);
    }

    @Test
    @DisplayName("비동기 예외 핸들러는 예외 발생 시 던지지 않고 처리한다")
    void asyncUncaughtExceptionHandler_handlesExceptionSilently() throws NoSuchMethodException {
        AsyncUncaughtExceptionHandler handler = asyncConfig.getAsyncUncaughtExceptionHandler();
        Method dummyMethod = Object.class.getMethod("toString");

        assertDoesNotThrow(() ->
                handler.handleUncaughtException(new RuntimeException("boom"), dummyMethod, new Object[0]));
    }

    @Test
    @DisplayName("비동기 예외 핸들러는 매번 새로운 인스턴스가 아니어도 동일하게 동작한다")
    void asyncUncaughtExceptionHandler_isNotNull() {
        assertThat(asyncConfig.getAsyncUncaughtExceptionHandler()).isNotNull();
    }
}