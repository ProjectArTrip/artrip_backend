package org.atdev.artrip.infra.fcm.service.dto;

public record NoticeCreatedEvent(
        String title,
        String content
) {
}
