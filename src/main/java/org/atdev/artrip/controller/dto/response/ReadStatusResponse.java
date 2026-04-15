package org.atdev.artrip.controller.dto.response;

public record ReadStatusResponse(boolean unread) {
    public static ReadStatusResponse of(boolean unread) {
        return new ReadStatusResponse(unread);
    }
}
