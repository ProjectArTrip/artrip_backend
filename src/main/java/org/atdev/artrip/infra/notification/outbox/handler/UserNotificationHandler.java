package org.atdev.artrip.infra.notification.outbox.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * 사용자 한 명에게 가는 알림(리뷰 승인/반려/삭제, 전시 제보 접수/등록).
 */
@Component
@RequiredArgsConstructor
public class UserNotificationHandler implements NotificationHandler<Object> {

    private final UserNoticeService userNoticeService;
    private final FcmNotificationService fcmNotificationService;

    @Override
    public Set<OutboxEventType> types() {
        return EnumSet.of(
                OutboxEventType.REVIEW_APPROVED,
                OutboxEventType.REVIEW_REJECTED,
                OutboxEventType.REVIEW_DELETED_BY_ADMIN,
                OutboxEventType.EXHIBIT_REPORT_CREATED,
                OutboxEventType.EXHIBIT_REPORT_REGISTERED
        );
    }

    @Override
    public void saveInbox(Object event) {
        Target target = target(event);
        userNoticeService.saveSingle(target.userId(), target.message());
    }

    @Override
    public void sendPush(Object event, PushProgress progress) throws FirebaseMessagingException {
        if (progress.cursor() != null) return;

        Target target = target(event);
        fcmNotificationService.sendToUser(target.userId(), target.message());
        progress.advance(target.userId());
    }

    private static Target target(Object event) {
        if (event instanceof ReviewApprovedEvent e) {
            return new Target(e.userId(), message(
                    NotificationTemplate.REVIEW_APPROVED,
                    NotificationReference.exhibit(e.exhibitId()),
                    e.exhibitTitle()));
        }
        if (event instanceof ReviewRejectedEvent e) {
            return new Target(e.userId(), message(
                    NotificationTemplate.REVIEW_REJECTED,
                    NotificationReference.reviewEdit(e.reviewId()),
                    e.exhibitTitle(), e.reviewContent(), e.reason()));
        }
        if (event instanceof ReviewDeleteByAdminEvent e) {
            return new Target(e.userId(), message(
                    NotificationTemplate.REVIEW_DELETED_BY_ADMIN,
                    NotificationReference.none()));
        }
        if (event instanceof ExhibitReportCreatedEvent e) {
            return new Target(e.userId(), message(
                    NotificationTemplate.EXHIBIT_REPORT_RECEIVED,
                    NotificationReference.none(),
                    e.title()));
        }
        if (event instanceof ExhibitReportRegisteredEvent e) {
            return new Target(e.userId(), message(
                    NotificationTemplate.EXHIBIT_REPORT_REGISTERED,
                    NotificationReference.exhibit(e.exhibitId()),
                    e.title()));
        }
        throw new IllegalArgumentException("단건 알림 대상이 아닌 이벤트: " + event.getClass().getName());
    }

    private static NotificationMessage message(NotificationTemplate tpl, NotificationReference reference, Object... args) {
        return NotificationMessage.of(reference, tpl.getTitle(), tpl.formatBody(args));
    }

    private record Target(Long userId, NotificationMessage message) {
    }
}
