package org.atdev.artrip.infra.notification.outbox.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.infra.notification.outbox.OutboxEventType;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.UserNoticeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExhibitCreatedNotificationHandler implements NotificationHandler<ExhibitCreatedEvent> {

    private final UserNoticeService userNoticeService;
    private final FcmNotificationService fcmNotificationService;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Override
    public Set<OutboxEventType> types() {
        return Set.of(OutboxEventType.EXHIBIT_CREATED);
    }

    @Override
    public void saveInbox(ExhibitCreatedEvent event) {
        if (event.isMultiple()) {
            userNoticeService.broadcast(multipleMessage(event));
            return;
        }

        ExhibitCreatedEvent.ExhibitSummary exhibit = event.representative();
        List<User> allPushUsers = userRepository.findValidPushUsers();
        if (allPushUsers.isEmpty()) return;

        Map<Boolean, List<User>> partitioned = partitionByKeyword(allPushUsers, matchedUserIds(exhibit));
        userNoticeService.saveAllPersonalized(partitioned.get(true), personalizedMessage(exhibit),
                NotificationTemplate.EXHIBIT_NEW_PERSONALIZED.getTitle());
        userNoticeService.saveAll(partitioned.get(false), genericMessage(exhibit));
    }

    @Override
    public void sendPush(ExhibitCreatedEvent event, PushProgress progress) throws FirebaseMessagingException {
        if (event.isMultiple()) {
            fcmNotificationService.sendBroadcast(multipleMessage(event), progress);
            return;
        }

        ExhibitCreatedEvent.ExhibitSummary exhibit = event.representative();
        Set<Long> matchedUserIds = matchedUserIds(exhibit);
        NotificationMessage personalizedMessage = personalizedMessage(exhibit);
        NotificationMessage genericMessage = genericMessage(exhibit);

        fcmNotificationService.forEachPushUserChunk(progress, users -> {
            Map<Boolean, List<User>> partitioned = partitionByKeyword(users, matchedUserIds);
            fcmNotificationService.sendToUsers(partitioned.get(true), personalizedMessage);
            fcmNotificationService.sendToUsers(partitioned.get(false), genericMessage);
        });
    }

    private Set<Long> matchedUserIds(ExhibitCreatedEvent.ExhibitSummary exhibit) {
        return exhibit.keywordIds().isEmpty()
                ? Set.of()
                : userKeywordRepository.findUserIdsWithKeywordIn(exhibit.keywordIds());
    }

    private static Map<Boolean, List<User>> partitionByKeyword(List<User> users, Set<Long> matchedUserIds) {
        return users.stream().collect(Collectors.partitioningBy(u -> matchedUserIds.contains(u.getUserId())));
    }

    private static NotificationMessage multipleMessage(ExhibitCreatedEvent event) {
        ExhibitCreatedEvent.ExhibitSummary rep = event.representative();
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_NEW_MULTIPLE;
        return NotificationMessage.of(
                NotificationReference.exhibit(rep.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(rep.title(), event.additionalCount())
        );
    }

    private static NotificationMessage personalizedMessage(ExhibitCreatedEvent.ExhibitSummary exhibit) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_NEW_PERSONALIZED;
        return NotificationMessage.of(
                NotificationReference.exhibit(exhibit.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(exhibit.hallName(), exhibit.title())
        );
    }

    private static NotificationMessage genericMessage(ExhibitCreatedEvent.ExhibitSummary exhibit) {
        NotificationTemplate tpl = NotificationTemplate.EXHIBIT_NEW_SINGLE;
        return NotificationMessage.of(
                NotificationReference.exhibit(exhibit.exhibitId()),
                tpl.getTitle(),
                tpl.formatBody(exhibit.hallName(), exhibit.title())
        );
    }
}
