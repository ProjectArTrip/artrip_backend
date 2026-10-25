package org.atdev.artrip.infra.notification.outbox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OutboxEventType {
    NOTICE_CREATED(NoticeCreatedEvent.class),
    EXHIBIT_CREATED(ExhibitCreatedEvent.class),
    REVIEW_APPROVED(ReviewApprovedEvent.class),
    REVIEW_REJECTED(ReviewRejectedEvent.class),
    REVIEW_DELETED_BY_ADMIN(ReviewDeleteByAdminEvent.class),
    EXHIBIT_REPORT_CREATED(ExhibitReportCreatedEvent.class),
    EXHIBIT_REPORT_REGISTERED(ExhibitReportRegisteredEvent.class);

    private final Class<?> eventClass;

    public static OutboxEventType from(Class<?> eventClass) {
        return Arrays.stream(values())
                .filter(type -> type.eventClass.equals(eventClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("outbox 대상이 아닌 이벤트: " + eventClass.getName()));
    }
}
