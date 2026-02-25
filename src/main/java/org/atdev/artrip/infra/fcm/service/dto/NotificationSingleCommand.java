package org.atdev.artrip.infra.fcm.service.dto;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.NonNull;

public record NotificationSingleCommand(
        @NonNull String targetToken,
        String title,
        String body) implements NotificationCommand {

    public static NotificationSingleCommand of(String token, String title, String body) {
        return new NotificationSingleCommand(
                token,
                title,
                body
        );
    }

    public Message.Builder builderMessage() {
        return Message.builder()
                .setToken(targetToken)
                .setNotification(toNotification());
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

}
