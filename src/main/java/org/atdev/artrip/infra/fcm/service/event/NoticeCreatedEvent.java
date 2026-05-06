package org.atdev.artrip.infra.fcm.service.event;

public record NoticeCreatedEvent(
        Long noticeId,
        String title,
        String content
) {
}
