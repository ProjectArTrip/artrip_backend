package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.Notification;
import lombok.NonNull;

import java.util.Map;

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
}
