package org.atdev.artrip.infra.fcm.service.event;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.infra.fcm.service.FcmNotificationService;
import org.atdev.artrip.infra.fcm.service.dto.NoticeCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmNoticeCreatedEventListener {

    private final FcmNotificationService fcmNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NoticeCreatedEvent event) {
        fcmNotificationService.sendNoticeMessage(event.title(), event.content());
    }
}
