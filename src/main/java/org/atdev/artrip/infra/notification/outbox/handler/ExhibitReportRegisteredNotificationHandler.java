package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.domain.exhibitReport.ExhibitReportRegisteredEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

@Component
public class ExhibitReportRegisteredNotificationHandler extends SingleUserNotificationHandler<ExhibitReportRegisteredEvent> {

    public ExhibitReportRegisteredNotificationHandler(UserNoticeService userNoticeService, FcmNotificationService fcmNotificationService) {
        super(userNoticeService, fcmNotificationService);
    }

    @Override
    public OutboxEventType type() {
        return OutboxEventType.EXHIBIT_REPORT_REGISTERED;
    }

    @Override
    protected Long userId(ExhibitReportRegisteredEvent event) {
        return event.userId();
    }

    @Override
    protected NotificationMessage message(ExhibitReportRegisteredEvent event) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_REPORT_REGISTERED;
        return NotificationMessage.of(
                NotificationReference.exhibit(event.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(event.title())
        );
    }
}
