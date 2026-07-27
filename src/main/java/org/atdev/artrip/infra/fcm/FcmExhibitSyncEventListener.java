package org.atdev.artrip.infra.fcm;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibitSync.ExhibitSyncedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmExhibitSyncEventListener {

    private final FcmNotificationService fcmNotificationService;
    private final UserNoticeService userNoticeService;
    private final UserRepository userRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSynced(ExhibitSyncedEvent event) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_SYNC_WEEKLY;
        NotificationMessage message = NotificationMessage.of(
                NotificationReference.exhibitList(),
                tpl.getTitle(),
                tpl.formatBody(event.newCount())
        );
        List<User> pushUsers = userRepository.findValidPushUsers();
        userNoticeService.saveAll(pushUsers, message);
        fcmNotificationService.sendMulticastToUsers(pushUsers, message);
    }
}
