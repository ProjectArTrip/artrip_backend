package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.domain.notice.Notice;

import java.time.LocalDateTime;

public record AdminNoticeListResponse(
        Long noticeId,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AdminNoticeListResponse from(Notice notice) {
        return new AdminNoticeListResponse(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
