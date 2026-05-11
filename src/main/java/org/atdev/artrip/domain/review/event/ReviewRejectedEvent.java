package org.atdev.artrip.domain.review.event;

public record ReviewRejectedEvent(
        Long reviewId,
        Long userId,
        String exhibitTitle,
        String reviewContent,
        String reason
) {
}
