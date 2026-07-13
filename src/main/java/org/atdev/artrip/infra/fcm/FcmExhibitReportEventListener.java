package org.atdev.artrip.infra.fcm;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmExhibitReportEventListener {

    private final FcmNotificationService fcmService;
    private final UserNoticeService userNoticeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ExhibitReportCreatedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_REPORT_RECEIVED;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.none(),
                tpl.getTitle(),
                tpl.formatBody(event.title())
        );

        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRegistered(ExhibitReportRegisteredEvent event) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_REPORT_REGISTERED;

        NotificationMessage message = NotificationMessage.of(
                NotificationReference.exhibit(event.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(event.title())
        );

        userNoticeService.saveSingle(event.userId(), message);
        fcmService.sendToUser(event.userId(), message);
    }

}
