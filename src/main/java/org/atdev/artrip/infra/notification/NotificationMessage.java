package org.atdev.artrip.infra.notification;

public record NotificationMessage(
        NotificationReference reference,
        String title,
        String body
) {
    public static NotificationMessage of(NotificationReference reference, String title, String body){
        return new NotificationMessage(reference, title, body);
    }
}
