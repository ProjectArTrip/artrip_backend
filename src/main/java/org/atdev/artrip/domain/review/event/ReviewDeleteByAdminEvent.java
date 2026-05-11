package org.atdev.artrip.domain.review.event;

public record ReviewDeleteByAdminEvent(
        Long reviewId,
        Long userId,
        String exhibitTitle
) {
}
