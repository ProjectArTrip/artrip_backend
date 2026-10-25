package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.PushProgress;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FcmNotificationService {

    /** FCM multicast 한 번에 보낼 수 있는 최대 토큰 수 */
    static final int MULTICAST_LIMIT = 500;

    private static final String PUSH_SERVICE_TITLE = "ArtTrip";

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

    public void sendToUser(Long userId, NotificationMessage message) throws FirebaseMessagingException {
        Optional<String> token = userRepository.findById(userId)
                .map(User::getFcmToken)
                .filter(FcmNotificationService::isValidToken);
        if (token.isEmpty()) return;

        try {
            messageSender.sendSingle(NotificationSingleDispatch.of(
                    token.get(),
                    PUSH_SERVICE_TITLE,
                    message.body(),
                    message.reference().toFcmData()));
        } catch (FirebaseMessagingException e) {
            if (shouldInvalidate(e.getMessagingErrorCode())) {
                invalidateToken(token.get());
                return;
            }
            throw e;
        }
    }

    public void sendBroadcast(NotificationMessage message, PushProgress progress) throws FirebaseMessagingException {
        forEachPushUserChunk(progress, users -> sendToUsers(users, message));
    }

    /**
     * 푸시 가능한 사용자를 user_id 순으로 500명씩 넘겨주고, 청크를 보낼 때마다 progress를 전진시킨다.
     */
    public void forEachPushUserChunk(PushProgress progress, UserChunkSender sender) throws FirebaseMessagingException {
        Long cursor = progress.cursor() == null ? 0L : progress.cursor();

        while (true) {
            List<User> users = userRepository.findValidPushUsersAfter(cursor, PageRequest.of(0, MULTICAST_LIMIT));
            if (users.isEmpty()) return;

            sender.send(users);
            cursor = users.get(users.size() - 1).getUserId();
            progress.advance(cursor);

            if (users.size() < MULTICAST_LIMIT) return;
        }
    }

    public void sendToUsers(List<User> users, NotificationMessage message) throws FirebaseMessagingException {
        List<String> tokens = users.stream()
                .map(User::getFcmToken)
                .filter(FcmNotificationService::isValidToken)
                .distinct().toList();

        for (int from = 0; from < tokens.size(); from += MULTICAST_LIMIT) {
            List<String> chunk = tokens.subList(from, Math.min(from + MULTICAST_LIMIT, tokens.size()));
            BatchResponse response = messageSender.sendMulticast(
                    NotificationMulticastDispatch.of(
                            chunk,
                            PUSH_SERVICE_TITLE,
                            message.body(),
                            message.reference().toFcmData()));
            handleBatchResponse(chunk, response);
        }
    }

    public void invalidateToken(String token) {
        transactionTemplate.executeWithoutResult(status ->
                userRepository.findByFcmToken(token).ifPresent(User::clearFcmToken));
    }

    private void handleBatchResponse(List<String> tokens, BatchResponse response) {
        if (response.getFailureCount() == 0) return;

        int otherFailures = 0;
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sr = responses.get(i);
            if (sr.isSuccessful()) continue;

            FirebaseMessagingException e = sr.getException();
            if (e != null && shouldInvalidate(e.getMessagingErrorCode())) {
                invalidateToken(tokens.get(i));
            } else {
                otherFailures++;
            }
        }

        if (otherFailures > 0) {
            log.warn("FCM multicast 일부 실패: tokens={}, failed={}", tokens.size(), otherFailures);
        }
    }

    private static boolean isValidToken(String token) {
        return token != null && !token.isBlank();
    }

    private static boolean shouldInvalidate(MessagingErrorCode code) {
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.SENDER_ID_MISMATCH
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    @FunctionalInterface
    public interface UserChunkSender {
        void send(List<User> users) throws FirebaseMessagingException;
    }
}
