package org.atdev.artrip.infra.fcm.service.dto;

import com.google.firebase.messaging.Notification;

public interface NotificationCommand {
    String title();
    String body();
    Notification toNotification();
}
