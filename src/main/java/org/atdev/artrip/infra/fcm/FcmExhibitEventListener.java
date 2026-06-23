package org.atdev.artrip.infra.fcm;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FcmExhibitEventListener {

    private final FcmNotificationService fcmNotificationService;
    private final UserNoticeService userNoticeService;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ExhibitCreatedEvent event) {

        if (event.isMultiple()) {
            handleMultiple(event);
        } else {
            handleSingle(event.representative());
        }
    }

    private void handleMultiple(ExhibitCreatedEvent event) {
        ExhibitCreatedEvent.ExhibitSummary rep = event.representative();
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_NEW_MULTIPLE;
        NotificationMessage message = NotificationMessage.of(
                NotificationReference.exhibit(rep.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(rep.title(), event.additionalCount())
                );
        userNoticeService.broadcast(message);
        fcmNotificationService.sendBroadcast(message);
    }

    private void handleSingle(ExhibitCreatedEvent.ExhibitSummary exhibit) {
        List<User> allPushUsers = userRepository.findValidPushUsers();
        if (allPushUsers.isEmpty()) return;

        Set<Long> matchedUserIds = exhibit.keywordIds().isEmpty() ? Set.of() : userKeywordRepository.findUserIdsWithKeywordIn(exhibit.keywordIds());
        List<User> personalizedUsers = allPushUsers.stream()
                .filter(u -> matchedUserIds.contains(u.getUserId())).toList();
        List<User> genericUsers = allPushUsers.stream()
                .filter(u -> !matchedUserIds.contains(u.getUserId())).toList();

        NotificationMessage personalizedMessage = NotificationMessage.of(
                NotificationReference.exhibit(exhibit.exhibitId()),
                NotificationTemplate.EXHIBIT_NEW_PERSONALIZED.getTitle(),
                NotificationTemplate.EXHIBIT_NEW_PERSONALIZED.formatBody(exhibit.hallName(), exhibit.title())
        );

        userNoticeService.saveAllPersonalized(personalizedUsers, personalizedMessage, NotificationTemplate.EXHIBIT_NEW_PERSONALIZED.getTitle());
        fcmNotificationService.sendMulticastToUsers(personalizedUsers, personalizedMessage);

        NotificationMessage genericMessage = NotificationMessage.of(
                NotificationReference.exhibit(exhibit.exhibitId()),
                NotificationTemplate.EXHIBIT_NEW_SINGLE.getTitle(),
                NotificationTemplate.EXHIBIT_NEW_SINGLE.formatBody(exhibit.hallName(), exhibit.title())
        );
        userNoticeService.saveAll(genericUsers, genericMessage);
        fcmNotificationService.sendMulticastToUsers(genericUsers, genericMessage);


    }

}
