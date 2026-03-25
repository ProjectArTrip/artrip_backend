package org.atdev.artrip.controller.dto.request;

public record AdminUpdateNoticeRequest(
        String title,
        String content
) {
}
