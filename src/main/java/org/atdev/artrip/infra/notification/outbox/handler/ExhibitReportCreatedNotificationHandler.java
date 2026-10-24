package org.atdev.artrip.infra.notification.outbox.handler;

import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

@Component
public class ExhibitReportCreatedNotificationHandler extends SingleUserNotificationHandler<ExhibitReportCreatedEvent> {

    public ExhibitReportCreatedNotificationHandler(UserNoticeService userNoticeService, FcmNotificationService fcmNotificationService) {
        super(userNoticeService, fcmNotificationService);
    }

    @Override
    public OutboxEventType type() {
        return OutboxEventType.EXHIBIT_REPORT_CREATED;
    }

    @Override
    protected Long userId(ExhibitReportCreatedEvent event) {
        return event.userId();
    }

    @Override
    protected NotificationMessage message(ExhibitReportCreatedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_REPORT_RECEIVED;
        return NotificationMessage.of(
                NotificationReference.none(),
                tpl.getTitle(),
                tpl.formatBody(event.title())
        );
    }
}
