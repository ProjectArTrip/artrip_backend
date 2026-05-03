package org.atdev.artrip.infra.fcm.service;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.FcmErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.service.dto.NotificationCommand;
import org.atdev.artrip.infra.fcm.service.dto.NotificationMulticastCommand;
import org.atdev.artrip.infra.fcm.service.dto.NotificationSingleCommand;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
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

    public void sendMessage(final NotificationSingleCommand command) {
        try {
            Message message = command.builderMessage()
                    .setApnsConfig(getApnsConfig(command))
                    .build();

            ApiFutures.addCallback(
                    firebaseMessaging.sendAsync(message),
                    new ApiFutureCallback<>() {
                        public void onSuccess(String msgId) {
                            log.debug("FCM sent: {}", msgId);
                        }

                        public void onFailure(Throwable t) {
                            log.warn("FCM failed: {}", command.targetToken(), t);
                            if (t instanceof FirebaseMessagingException fmc) {
                                MessagingErrorCode code = fmc.getMessagingErrorCode();
                                if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                                    invalidateToken(command.targetToken());
                                }
                            }
                        }
                    },
                    executor
            );


        } catch (Exception e) {
            log.error("FCM 예외 발생 : exception : {}", e.getMessage(), e);
            throw new GeneralException(FcmErrorCode._FCM_SERVER_ERROR, e);
        }
    }

    public void sendMessage(NotificationMulticastCommand command) {
        try {
            MulticastMessage message = command.buildSendMessage()
                    .setApnsConfig(getApnsConfig(command))
                    .build();

            ApiFutures.addCallback(
                    firebaseMessaging.sendEachForMulticastAsync(message),
                    new ApiFutureCallback<>() {
                        public void onSuccess(BatchResponse response) {
                            log.debug("FCM multicast sent: success={}, fail={}", response.getSuccessCount(), response.getFailureCount());

                            if (response.getFailureCount() == 0) return;

                            List<SendResponse> responses = response.getResponses();
                            List<String> tokens = command.targetToken();

                            for (int i = 0; i < responses.size(); i++) {
                                SendResponse sr = responses.get(i);
                                if(!sr.isSuccessful() && sr.getException() != null) {
                                    MessagingErrorCode code = sr.getException().getMessagingErrorCode();
                                    if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                                        invalidateToken(tokens.get(i));
                                    }
                                }
                            }
                        }

                        public void onFailure(Throwable t) {
                            log.warn("FCM failed: {}", command.targetToken(), t);
                        }
                    },
                    executor
            );


        } catch (Exception e) {
            log.error("FCM 예외 발생 : exception : {}", e.getMessage(), e);
            throw new GeneralException(FcmErrorCode._FCM_SERVER_ERROR, e);
        }
    }

    private ApnsConfig getApnsConfig(NotificationCommand command) {

        ApsAlert alert = ApsAlert.builder()
                .setTitle(command.title())
                .setBody(command.body()).build();

        Aps aps = Aps.builder()
                .setAlert(alert)
                .setSound("default")
                .setBadge(1)
                .build();

        return ApnsConfig.builder()
                .setAps(aps)
                .putHeader("apns-priority", "10")
                .putHeader("apns-push-type", "alert")
                .build();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void sendNoticeMessage(Long noticeId, String title, String content) {
        List<String> tokens = userRepository.findValidPushUsers().stream()
                .map(User::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .distinct()
                .toList();
        if (tokens.isEmpty()) {
            return;
        }

        sendMessage(NotificationMulticastCommand.of(
                tokens,
                PUSH_SERVICE_TITLE,
                "[공지]" + title + "\n" + content,
                Map.of("action", NotificationAction.MOVE_NOTICE_DETAIL.getAction(),
                        "referenceId", String.valueOf(noticeId))));
    }

    public void invalidateToken(String token) {
        transactionTemplate.executeWithoutResult(status ->
            userRepository.findByFcmToken(token).ifPresent(User::clearFcmToken));
    }
}
