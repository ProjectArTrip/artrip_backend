package org.atdev.artrip.infra.notification.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 커밋 직후 별도 스레드에서 바로 발송을 시도한다.
 * 여기서 실패하거나 실행되지 못한 행은 OutboxPoller가 다시 처리한다.
 */
@Component
@RequiredArgsConstructor
public class OutboxImmediateDispatcher {

    private final OutboxProcessor outboxProcessor;

    @Async("outboxExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatch(OutboxRecordedEvent event) {
        outboxProcessor.process(event.outboxId());
    }
}
