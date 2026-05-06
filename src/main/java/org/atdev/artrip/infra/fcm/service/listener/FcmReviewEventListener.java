package org.atdev.artrip.infra.fcm.service.listener;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.ReviewRejectionReason;
import org.atdev.artrip.global.apipayload.code.status.ReviewNotificationMessage;
import org.atdev.artrip.infra.fcm.service.FcmNotificationService;
import org.atdev.artrip.infra.fcm.service.event.ReviewApprovedEvent;
import org.atdev.artrip.infra.fcm.service.event.ReviewDeleteByAdminEvent;
import org.atdev.artrip.infra.fcm.service.event.ReviewRejectedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FcmReviewEventListener {

    private static final Map<ReviewRejectionReason, ReviewNotificationMessage> REJECT_MESSAGES = Map.of(
            ReviewRejectionReason.POLICY_VIOLATION, ReviewNotificationMessage.REVIEW_REJECTED_POLICY,
            ReviewRejectionReason.BANNED_WORD, ReviewNotificationMessage.REVIEW_REJECTED_BANNED_WORD
    );

    private final FcmNotificationService fcmService;
    private final UserNoticeService userNoticeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ReviewDeleteByAdminEvent event) {
        ReviewNotificationMessage tpl = ReviewNotificationMessage.REVIEW_DELETED_BY_ADMIN;

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
        ReviewNotificationMessage tpl = REJECT_MESSAGES.get(event.reason());

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.reviewEdit(event.reviewId()),
                tpl.getTitle(),
                tpl.getBody()
        );

        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApproved(ReviewApprovedEvent event) {
        ReviewNotificationMessage tpl = ReviewNotificationMessage.REVIEW_APPROVED;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.exhibit(event.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(event.exhibitTitle())
        );
        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }



}