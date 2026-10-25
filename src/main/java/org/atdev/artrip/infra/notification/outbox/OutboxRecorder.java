package org.atdev.artrip.infra.notification.outbox;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.repository.NotificationOutboxRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 알림 이벤트를 발행한 비즈니스 트랜잭션 안에서 outbox 행을 함께 저장한다.
 * outbox 저장이 실패하면 비즈니스 데이터도 롤백되어, 알림 없이 데이터만 남는 상태가 생기지 않는다.
 */
@Component
@RequiredArgsConstructor
public class OutboxRecorder {

    private final NotificationOutboxRepository outboxRepository;
    private final OutboxPayloadMapper payloadMapper;
    private final ApplicationEventPublisher publisher;

    @EventListener({ //  공지사항 책임 분리를 위해 이벤트로 나누었음 !공지사항에 outboxRepository가 들어가면 srp원칙 위배
            NoticeCreatedEvent.class,//NoticeCreatedEvent 이벤트 발생시 수신
            ExhibitCreatedEvent.class,
            ReviewApprovedEvent.class,
            ReviewRejectedEvent.class,
            ReviewDeleteByAdminEvent.class,
            ExhibitReportCreatedEvent.class,
            ExhibitReportRegisteredEvent.class
    })
    public void record(Object event) {
        OutboxEventType type = OutboxEventType.from(event.getClass());
        NotificationOutbox outbox = outboxRepository.save(
                NotificationOutbox.pending(type, payloadMapper.write(event), LocalDateTime.now()));

        publisher.publishEvent(new OutboxRecordedEvent(outbox.getOutboxId()));
    }
}
