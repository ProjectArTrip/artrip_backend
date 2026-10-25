package org.atdev.artrip.infra.notification.outbox;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.transaction.CannotCreateTransactionException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OutboxRetryPolicyTest {

    @Test
    @DisplayName("FCM 일시 장애 코드는 재시도, 잘못된 요청은 재시도하지 않는다")
    void fcmErrorCode() {
        assertThat(OutboxRetryPolicy.isRetryable(fcmException(ErrorCode.UNAVAILABLE))).isTrue();
        assertThat(OutboxRetryPolicy.isRetryable(fcmException(ErrorCode.INTERNAL))).isTrue();
        assertThat(OutboxRetryPolicy.isRetryable(fcmException(ErrorCode.INVALID_ARGUMENT))).isFalse();
        assertThat(OutboxRetryPolicy.isRetryable(fcmException(ErrorCode.PERMISSION_DENIED))).isFalse();
    }

    @Test
    @DisplayName("DB 일시 오류는 재시도, 코드/데이터 오류는 재시도하지 않는다")
    void dataAccessAndLogicError() {
        assertThat(OutboxRetryPolicy.isRetryable(new QueryTimeoutException("timeout"))).isTrue();
        assertThat(OutboxRetryPolicy.isRetryable(new CannotCreateTransactionException("no connection"))).isTrue();
        assertThat(OutboxRetryPolicy.isRetryable(new IllegalStateException("bad payload"))).isFalse();
    }

    @Test
    @DisplayName("원인 예외(cause)까지 따라가서 판단한다")
    void wrappedCause() {
        RuntimeException wrapped = new RuntimeException("wrapped", new QueryTimeoutException("timeout"));
        assertThat(OutboxRetryPolicy.isRetryable(wrapped)).isTrue();
    }

    @Test
    @DisplayName("백오프는 차수에 따라 늘어나고 마지막 값에서 멈춘다")
    void backoff() {
        assertThat(OutboxRetryPolicy.backoff(1)).isEqualTo(Duration.ofSeconds(10));
        assertThat(OutboxRetryPolicy.backoff(2)).isEqualTo(Duration.ofSeconds(30));
        assertThat(OutboxRetryPolicy.backoff(5)).isEqualTo(Duration.ofMinutes(30));
        assertThat(OutboxRetryPolicy.backoff(9)).isEqualTo(Duration.ofMinutes(30));
    }

    private static FirebaseMessagingException fcmException(ErrorCode code) {
        FirebaseMessagingException e = mock(FirebaseMessagingException.class);
        when(e.getErrorCode()).thenReturn(code);
        return e;
    }
}
