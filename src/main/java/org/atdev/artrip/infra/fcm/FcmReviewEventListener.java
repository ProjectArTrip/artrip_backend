package org.atdev.artrip.infra.fcm;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.domain.review.event.ReviewApprovedEvent;
import org.atdev.artrip.domain.review.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.domain.review.event.ReviewRejectedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmReviewEventListener {

    private final FcmNotificationService fcmService;
    private final UserNoticeService userNoticeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ReviewDeleteByAdminEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_DELETED_BY_ADMIN;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.none(),
                tpl.getTitle(),
                tpl.getBody()
        );
        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRejected(ReviewRejectedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_REJECTED;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.reviewEdit(event.reviewId()),
                tpl.getTitle(),
                tpl.formatBody(event.exhibitTitle(), event.reviewContent(), event.reason())
        );

        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApproved(ReviewApprovedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.REVIEW_APPROVED;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.exhibit(event.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(event.exhibitTitle())
        );
        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }



}