package org.atdev.artrip.domain.notice.event;

public record NoticeCreatedEvent(
        Long noticeId,
        String title,
        String content
) {
}
