package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.*;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface NotificationDispatch {
    String title();

    String body();

    Notification toNotification();

    Map<String, String> data();

    void send(FirebaseMessaging messaging, Executor executor, Consumer<String> tokenInvalidator);

    default ApnsConfig toApnsConfig() {
        ApsAlert alert = ApsAlert.builder()
                .setTitle(title()).setBody(body())
                .build();
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
}
