package org.atdev.artrip.infra.fcm.service.dto;

public record NoticeCreatedEvent(
        Long noticeId,
        String title,
        String content
) {
}
