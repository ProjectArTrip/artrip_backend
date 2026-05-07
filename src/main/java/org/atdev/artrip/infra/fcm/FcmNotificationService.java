package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.error.FcmErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class FcmNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final Executor executor;
    private final TransactionTemplate transactionTemplate;

    public FcmNotificationService(
            FirebaseMessaging firebaseMessaging,
            UserRepository userRepository,
            @Qualifier("fcmExecutor") Executor executor,
            PlatformTransactionManager transactionManager) {
        this.firebaseMessaging = firebaseMessaging;
        this.userRepository = userRepository;
        this.executor = executor;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    private static final String PUSH_SERVICE_TITLE = "ArtTrip";

    public void sendMessage(NotificationDispatch dispatch) {
        try {
            dispatch.send(firebaseMessaging, executor, this::invalidateToken);
        } catch (Exception e) {
            log.error("FCM 예외 발생 : exception: {}", e.getMessage(), e);
            throw new GeneralException(FcmErrorCode._FCM_SERVER_ERROR, e);
        }
    }

    public void sendToUser(Long userId, NotificationMessage message) {
        userRepository.findById(userId)
                .map(User::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .ifPresent(token -> sendMessage(NotificationSingleDispatch.of(
                        token,
                        PUSH_SERVICE_TITLE,
                        message.body(),
                        message.reference().toFcmData()
                )));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void sendBroadcast(NotificationMessage message) {
        List<String> tokens = collectValidPushTokens();
        if (tokens.isEmpty()) return;

        sendMessage(NotificationMulticastDispatch.of(
                tokens,
                PUSH_SERVICE_TITLE,
                message.body(),
                message.reference().toFcmData()
        ));
    }

    public void invalidateToken(String token) {
        transactionTemplate.executeWithoutResult(status ->
            userRepository.findByFcmToken(token).ifPresent(User::clearFcmToken));
    }

    private List<String> collectValidPushTokens() {
        return userRepository.findValidPushUsers().stream()
                .map(User::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .distinct().toList();
    }
}
