package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.notice.UserNotice;
import org.springframework.data.domain.Slice;

import java.util.List;

public record UserNoticeCursorResult(
        List<UserNoticeResult> notifications,
        boolean hasNext,
        Long nextCursor
) {

    public static UserNoticeCursorResult of(Slice<UserNotice> slice) {
        List<UserNoticeResult> items = slice.getContent().stream()
                .map(UserNoticeResult::from)
                .toList();

        Long nextCursor = slice.hasNext() && !items.isEmpty()
                ? items.get(items.size() - 1).userNoticeId() : null;

        return new UserNoticeCursorResult(items, slice.hasNext(), nextCursor);
    }
}
