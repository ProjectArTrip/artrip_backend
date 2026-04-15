package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.UserNoticeCursorResult;

import java.util.List;

public record UserNoticeCursorResponse(
        List<UserNoticeResponse> notifications,
        boolean hasNext,
        Long nextCursor
) {

    public static UserNoticeCursorResponse from(UserNoticeCursorResult result) {
        return new UserNoticeCursorResponse(
                result.notifications().stream()
                        .map(UserNoticeResponse::from)
                        .toList(),
                result.hasNext(),
                result.nextCursor()
        );
    }
}
