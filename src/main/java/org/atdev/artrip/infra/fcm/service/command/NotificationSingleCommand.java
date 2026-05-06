package org.atdev.artrip.infra.fcm.service.command;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.NonNull;

import java.util.Map;

public record NotificationSingleCommand(
        @NonNull String targetToken,
        String title,
        String body,
        Map<String, String> data
) implements NotificationCommand {

    public static NotificationSingleCommand of(String token, String title, String body, Map<String, String> data) {
        return new NotificationSingleCommand(token, title, body, data);
    }

    public Message.Builder builderMessage() {
        return Message.builder()
                .setToken(targetToken)
                .setNotification(toNotification())
                .putAllData(data);
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

}
