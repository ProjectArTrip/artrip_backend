package org.atdev.artrip.domain.review.event;

public record ReviewApprovedEvent(
        Long reviewId,
        Long userId,
        Long exhibitId,
        String exhibitTitle
) {
}
