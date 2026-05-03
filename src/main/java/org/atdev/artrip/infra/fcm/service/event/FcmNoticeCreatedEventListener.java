package org.atdev.artrip.infra.fcm.service.event;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.infra.fcm.service.FcmNotificationService;
import org.atdev.artrip.infra.fcm.service.dto.NoticeCreatedEvent;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmNoticeCreatedEventListener {

    private final FcmNotificationService fcmNotificationService;
    private final UserNoticeService userNoticeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NoticeCreatedEvent event) {
        userNoticeService.createNoticeNotification(event);
        fcmNotificationService.sendNoticeMessage(event.title(), event.content());
    }
}
