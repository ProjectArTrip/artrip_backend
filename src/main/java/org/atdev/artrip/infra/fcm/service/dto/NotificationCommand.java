package org.atdev.artrip.infra.fcm.service.dto;

import com.google.firebase.messaging.Notification;

import java.util.Map;

public interface NotificationCommand {
    String title();
    String body();
    Notification toNotification();

    Map<String, String> data();
}
