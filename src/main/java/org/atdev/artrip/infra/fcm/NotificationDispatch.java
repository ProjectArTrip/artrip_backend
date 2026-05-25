package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.Notification;

import java.util.Map;

public sealed interface NotificationDispatch permits NotificationSingleDispatch, NotificationMulticastDispatch {
    String title();

    String body();

    Notification toNotification();

    Map<String, String> data();

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
