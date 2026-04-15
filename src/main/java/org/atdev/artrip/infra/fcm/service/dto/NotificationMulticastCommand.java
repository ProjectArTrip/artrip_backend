package org.atdev.artrip.infra.fcm.service.dto;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record NotificationMulticastCommand(
        @NotNull List<String> targetToken,
        String title,
        String body,
        Map<String, String> data
        ) implements NotificationCommand {

    public static NotificationMulticastCommand of(List<String> targetToken, String title, String body, Map<String, String> data) {
        return new NotificationMulticastCommand(targetToken, title, body, data);
    }

    public MulticastMessage.Builder buildSendMessage() {
        return MulticastMessage.builder()
                .setNotification(toNotification())
                .addAllTokens(targetToken)
                .putAllData(data);
    }

    public Notification  toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
