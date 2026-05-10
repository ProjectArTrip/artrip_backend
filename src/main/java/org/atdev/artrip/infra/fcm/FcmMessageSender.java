package org.atdev.artrip.infra.fcm;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.code.error.FcmErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@Component
public class FcmMessageSender {
    private final FirebaseMessaging firebaseMessaging;
    private final Executor executor;

    public FcmMessageSender(FirebaseMessaging firebaseMessaging,
                            @Qualifier("fcmExecutor") Executor executor){
        this.firebaseMessaging = firebaseMessaging;
        this.executor = executor;
    }

    public void sendSingle(NotificationSingleDispatch dispatch, Consumer<String> tokenInvalidator) {
        try {
            Message message = Message.builder()
                    .setToken(dispatch.targetToken())
                    .setNotification(dispatch.toNotification())
                    .putAllData(dispatch.data())
                    .setApnsConfig(dispatch.toApnsConfig())
                    .build();

            ApiFutures.addCallback(
                    firebaseMessaging.sendAsync(message),
                    new ApiFutureCallback<>() {
                        @Override
                        public void onFailure(Throwable t) {
                            log.warn("FCM failed: {}", mask(dispatch.targetToken()), t);
                            if (t instanceof FirebaseMessagingException fmc && shouldInvalidate(fmc.getMessagingErrorCode())) {
                                tokenInvalidator.accept(dispatch.targetToken());
                            }
                        }

                        @Override
                        public void onSuccess(String result) {
                            log.debug("FCM sent: {}", result);
                        }
                    },
                    executor
            );
        } catch (Exception e) {
            log.error("FCM 예외 : exception: {}", e.getMessage(), e);
            throw new GeneralException(FcmErrorCode._FCM_SERVER_ERROR, e);
        }

    }

    public void sendMulticast(NotificationMulticastDispatch dispatch, Consumer<String> tokenInvalidator) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(dispatch.toNotification())
                    .addAllTokens(dispatch.targetTokens())
                    .putAllData(dispatch.data())
                    .setApnsConfig(dispatch.toApnsConfig())
                    .build();

            ApiFutures.addCallback(
                    firebaseMessaging.sendEachForMulticastAsync(message),
                    new ApiFutureCallback<>() {
                        @Override
                        public void onFailure(Throwable t) {
                            log.warn("FCM multicast failed : tokens= {}", dispatch.targetTokens().size(), t);
                        }

                        @Override
                        public void onSuccess(BatchResponse result) {
                            log.debug("FCM multicast sent: success={}, fail={}",
                                    result.getSuccessCount(), result.getFailureCount());
                            if (result.getFailureCount() == 0) return;

                            List<SendResponse> responses = result.getResponses();
                            for (int i = 0; i < responses.size(); i++) {
                                SendResponse sr = responses.get(i);
                                if (sr.isSuccessful() || sr.getException() == null) continue;
                                if (shouldInvalidate(sr.getException().getMessagingErrorCode())) {
                                    tokenInvalidator.accept(dispatch.targetTokens().get(i));
                                }
                            }
                        }
                    },
                    executor
            );
        } catch (Exception e) {
            log.error("FCM 예외 : exception: {}", e.getMessage(), e);
            throw new GeneralException(FcmErrorCode._FCM_SERVER_ERROR, e);
        }
    }

    private static boolean shouldInvalidate(MessagingErrorCode code){
        return code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.SENDER_ID_MISMATCH || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    private static String mask(String token) {
        if (token == null || token.length() < 8) return "***";
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }
}
