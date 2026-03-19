package org.atdev.artrip.infra.fcm.service.dto;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NotificationMulticastCommand(
        @NotNull List<String> targetToken,
        String title,
        String body
        ) implements NotificationCommand {

    public static NotificationMulticastCommand of(List<String> targetToken, String title, String body) {
        return new NotificationMulticastCommand(targetToken, title, body);
    }

    public MulticastMessage.Builder buildSendMessage() {
        return MulticastMessage.builder()
                .setNotification(toNotification())
                .addAllTokens(targetToken);
    }

    public Notification  toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
