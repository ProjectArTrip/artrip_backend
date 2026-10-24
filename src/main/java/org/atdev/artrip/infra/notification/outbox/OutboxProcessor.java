package org.atdev.artrip.infra.notification.outbox;

import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.handler.NotificationHandler;
import org.atdev.artrip.repository.NotificationOutboxRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * outbox 행 1건을 처리한다: 선점 → 알림함 저장 → 푸시 발송 → SENT.
 * 즉시 발송(OutboxImmediateDispatcher)과 재처리(OutboxPoller)가 모두 이 경로를 사용한다.
 */
@Slf4j
@Component
public class OutboxProcessor {

    /** 선점 유지 시간. 처리 중 서버가 죽으면 이 시간이 지난 뒤 폴러가 다시 가져간다. */
    static final Duration LEASE = Duration.ofMinutes(5);

    private final NotificationOutboxRepository outboxRepository;
    private final OutboxPayloadMapper payloadMapper;
    private final Map<OutboxEventType, NotificationHandler<?>> handlers;
    private final TransactionTemplate transactionTemplate;

    public OutboxProcessor(NotificationOutboxRepository outboxRepository,
                           OutboxPayloadMapper payloadMapper,
                           List<NotificationHandler<?>> handlers,
                           PlatformTransactionManager transactionManager) {
        this.outboxRepository = outboxRepository;
        this.payloadMapper = payloadMapper;
        this.handlers = new EnumMap<>(OutboxEventType.class);
        handlers.forEach(handler -> handler.types().forEach(type -> this.handlers.put(type, handler)));
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void process(Long outboxId) {
        if (!claim(outboxId)) return;

        NotificationOutbox outbox = outboxRepository.findById(outboxId).orElse(null);
        if (outbox == null) return;

        try {
            dispatch(outbox);
            update(outboxId, o -> o.markSent(LocalDateTime.now()));
        } catch (Exception e) {
            handleFailure(outbox, e);
        }
    }

    private boolean claim(Long outboxId) {
        LocalDateTime now = LocalDateTime.now();
        return Boolean.TRUE.equals(
                transactionTemplate.execute(status -> outboxRepository.claim(outboxId, now, now.plus(LEASE))));
    }

    @SuppressWarnings("unchecked")
    private <E> void dispatch(NotificationOutbox outbox) throws Exception {
        NotificationHandler<E> handler = (NotificationHandler<E>) handlers.get(outbox.getEventType());
        if (handler == null) {
            throw new IllegalStateException("알림 handler가 없습니다: " + outbox.getEventType());
        }
        E event = (E) payloadMapper.read(outbox.getEventType(), outbox.getPayload());

        if (!outbox.isInboxSaved()) {
            transactionTemplate.executeWithoutResult(status -> {
                handler.saveInbox(event);
                findOrThrow(outbox.getOutboxId()).markInboxSaved();
            });
        }

        handler.sendPush(event, new OutboxPushProgress(outbox.getOutboxId(), outbox.getPushCursor()));
    }

    private void handleFailure(NotificationOutbox outbox, Exception e) {
        int attempt = outbox.getRetryCount() + 1;
        boolean retry = OutboxRetryPolicy.isRetryable(e) && attempt <= OutboxRetryPolicy.MAX_RETRY;
        String error = e.getClass().getSimpleName() + ": " + e.getMessage();

        try {
            if (retry) {
                LocalDateTime nextRetryAt = LocalDateTime.now().plus(OutboxRetryPolicy.backoff(attempt));
                update(outbox.getOutboxId(), o -> o.scheduleRetry(error, nextRetryAt));
                log.warn("알림 발송 실패, 재시도 예약 outboxId={}, type={}, attempt={}/{}, nextRetryAt={}",
                        outbox.getOutboxId(), outbox.getEventType(), attempt, OutboxRetryPolicy.MAX_RETRY, nextRetryAt, e);
            } else {
                update(outbox.getOutboxId(), o -> o.markFailed(error, LocalDateTime.now()));
                log.error("알림 발송 최종 실패 outboxId={}, type={}, retryCount={}",
                        outbox.getOutboxId(), outbox.getEventType(), outbox.getRetryCount(), e);
            }
        } catch (Exception updateError) {
            // 상태 갱신마저 실패하면 PROCESSING으로 남고, 선점 만료 후 폴러가 다시 처리한다
            log.error("알림 outbox 상태 갱신 실패 outboxId={}", outbox.getOutboxId(), updateError);
        }
    }

    private void update(Long outboxId, Consumer<NotificationOutbox> change) {
        transactionTemplate.executeWithoutResult(status -> change.accept(findOrThrow(outboxId)));
    }

    private NotificationOutbox findOrThrow(Long outboxId) {
        return outboxRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalStateException("outbox가 없습니다: " + outboxId));
    }

    private class OutboxPushProgress implements PushProgress {

        private final Long outboxId;
        private Long cursor;

        private OutboxPushProgress(Long outboxId, Long cursor) {
            this.outboxId = outboxId;
            this.cursor = cursor;
        }

        @Override
        public Long cursor() {
            return cursor;
        }

        @Override
        public void advance(Long lastUserId) {
            LocalDateTime lockedUntil = LocalDateTime.now().plus(LEASE);
            update(outboxId, o -> o.advancePushCursor(lastUserId, lockedUntil));
            this.cursor = lastUserId;
        }
    }
}
