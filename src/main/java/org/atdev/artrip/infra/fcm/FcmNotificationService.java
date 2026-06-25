package org.atdev.artrip.infra.fcm;

import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
public class FcmNotificationService {

    private final UserRepository userRepository;
    private final FcmMessageSender messageSender;
    private final TransactionTemplate transactionTemplate;

    public FcmNotificationService(
            UserRepository userRepository,
            FcmMessageSender messageSender,
            PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.messageSender = messageSender;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    private static final String PUSH_SERVICE_TITLE = "ArtTrip";

    public void sendToUser(Long userId, NotificationMessage message) {
        userRepository.findById(userId)
                .map(User::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .ifPresent(token -> messageSender.sendSingle(
                        NotificationSingleDispatch.of(
                                token,
                                PUSH_SERVICE_TITLE,
                                message.body(),
                                message.reference().toFcmData()),
                        this::invalidateToken
                ));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void sendBroadcast(NotificationMessage message) {
        List<String> tokens = userRepository.findValidPushUsers().stream()
                .map(User::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .distinct().toList();
        if (tokens.isEmpty()) return;

        messageSender.sendMulticast(
                NotificationMulticastDispatch.of(
                        tokens,
                        PUSH_SERVICE_TITLE,
                        message.body(),
                        message.reference().toFcmData()),
                this::invalidateToken
        );
    }

    public void sendMulticastToUsers(List<User> users, NotificationMessage message) {
        List<String> tokens = users.stream()
                .map(User::getFcmToken)
                .filter(t -> t != null && !t.isBlank())
                .distinct().toList();
        if(tokens.isEmpty()) return;
        messageSender.sendMulticast(
                NotificationMulticastDispatch.of(
                        tokens,
                        PUSH_SERVICE_TITLE,
                        message.body(),
                        message.reference().toFcmData()),
                this::invalidateToken
        );
    }

    public void invalidateToken(String token) {
        transactionTemplate.executeWithoutResult(status ->
                userRepository.findByFcmToken(token).ifPresent(User::clearFcmToken));
    }
}
