package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.Notification;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

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
}
