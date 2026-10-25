package org.atdev.artrip.infra.notification.outbox;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.CannotCreateTransactionException;

import java.io.IOException;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 일시적인 장애(FCM 서버/네트워크, DB 커넥션)만 재시도하고,
 * 다시 해도 결과가 같은 오류(데이터 없음, 직렬화 실패, 잘못된 요청)는 즉시 FAILED 처리한다.
 */
public final class OutboxRetryPolicy {

    public static final int MAX_RETRY = 5;

    private static final List<Duration> BACKOFF = List.of(
            Duration.ofSeconds(10),
            Duration.ofSeconds(30),
            Duration.ofMinutes(2),
            Duration.ofMinutes(10),
            Duration.ofMinutes(30)
    );

    private static final Set<ErrorCode> RETRYABLE_FCM_CODES = EnumSet.of(
            ErrorCode.UNAVAILABLE,
            ErrorCode.INTERNAL,
            ErrorCode.DEADLINE_EXCEEDED,
            ErrorCode.RESOURCE_EXHAUSTED,
            ErrorCode.ABORTED,
            ErrorCode.UNKNOWN
    );

    private OutboxRetryPolicy() {
    }

    public static boolean isRetryable(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof FirebaseMessagingException fme) {
                return RETRYABLE_FCM_CODES.contains(fme.getErrorCode());
            }
            if (t instanceof TransientDataAccessException
                    || t instanceof RecoverableDataAccessException
                    || t instanceof CannotCreateTransactionException
                    || t instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    /** attempt: 1부터 시작하는 재시도 차수 */
    public static Duration backoff(int attempt) {
        int index = Math.min(Math.max(attempt, 1), BACKOFF.size()) - 1;
        return BACKOFF.get(index);
    }
}
