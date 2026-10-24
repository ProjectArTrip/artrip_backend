package org.atdev.artrip.infra.notification.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.repository.NotificationOutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 즉시 발송이 실패했거나(재시도 대기), 처리 중 서버가 죽어 선점이 만료된 행을 주워서 다시 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private static final int BATCH_SIZE = 20;
    private static final int SENT_RETENTION_DAYS = 7;

    private final NotificationOutboxRepository outboxRepository;
    private final OutboxProcessor outboxProcessor;

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void poll() {
        List<Long> outboxIds = outboxRepository.findProcessableIds(LocalDateTime.now(), BATCH_SIZE);
        if (outboxIds.isEmpty()) return;

        log.info("알림 outbox 재처리 대상 {}건", outboxIds.size());
        outboxIds.forEach(outboxProcessor::process);
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void cleanUpSent() {
        int deleted = outboxRepository.deleteProcessedBefore(
                OutboxStatus.SENT, LocalDateTime.now().minusDays(SENT_RETENTION_DAYS));
        log.info("발송 완료된 알림 outbox {}건 삭제", deleted);
    }
}
