package org.atdev.artrip.infra.fcm;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
public record NotificationSingleDispatch(
        @NonNull String targetToken,
        String title,
        String body,
        Map<String, String> data
) implements NotificationDispatch {

    public static NotificationSingleDispatch of(String token, String title, String body, Map<String, String> data) {
        return new NotificationSingleDispatch(token, title, body, data);
    }

    @Override
    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    @Override
    public void send(FirebaseMessaging messaging, Executor executor, Consumer<String> tokenInvalidator) {
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(toNotification())
                .putAllData(data)
                .setApnsConfig(toApnsConfig())
                .build();

        ApiFutures.addCallback(
                messaging.sendAsync(message),
                new ApiFutureCallback<String>() {
                    @Override
                    public void onFailure(Throwable t) {
                        log.warn("FCM failed: {}", targetToken, t);
                        if (t instanceof FirebaseMessagingException fmc) {
                            MessagingErrorCode code = fmc.getMessagingErrorCode();
                            if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                                tokenInvalidator.accept(targetToken);
                            }
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        log.debug("FCM sent: {}", result);
                    }
                },
                executor
        );
    }

}
