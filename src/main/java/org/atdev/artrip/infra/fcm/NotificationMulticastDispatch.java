package org.atdev.artrip.infra.fcm;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
public record NotificationMulticastDispatch(
        @NotNull List<String> targetTokens,
        String title,
        String body,
        Map<String, String> data
        ) implements NotificationDispatch {

    public static NotificationMulticastDispatch of(List<String> targetTokens, String title, String body, Map<String, String> data) {
        return new NotificationMulticastDispatch(targetTokens, title, body, data);
    }

    @Override
    public Notification  toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    @Override
    public void send(FirebaseMessaging messaging, Executor executor, Consumer<String> tokenInvalidator) {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(toNotification())
                .addAllTokens(targetTokens)
                .putAllData(data)
                .setApnsConfig(toApnsConfig())
                .build();

        ApiFutures.addCallback(
                messaging.sendEachForMulticastAsync(message),
                new ApiFutureCallback<BatchResponse>() {
                    @Override
                    public void onFailure(Throwable t) {
                        log.warn("FCM multicast failed : tokens= {}", targetTokens, t);
                    }

                    @Override
                    public void onSuccess(BatchResponse result) {
                        log.debug("FCM multicast sent: success={}, fail={}", result.getSuccessCount(), result.getFailureCount());
                        if (result.getFailureCount() == 0) return;

                        List<SendResponse> responses= result.getResponses();
                        for(int i = 0; i < responses.size(); i++) {
                            SendResponse sr = responses.get(i);
                            if(!sr.isSuccessful() && sr.getException() != null) {
                                MessagingErrorCode code = sr.getException().getMessagingErrorCode();
                                if (code == MessagingErrorCode.INVALID_ARGUMENT || code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.SENDER_ID_MISMATCH) {
                                    tokenInvalidator.accept(targetTokens.get(i));
                                }
                            }
                        }
                    }
                },
                executor
        );
    }
}
