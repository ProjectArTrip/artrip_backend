package org.atdev.artrip.infra.fcm.service.event;

public record ReviewApprovedEvent(
        Long reviewId,
        Long userId,
        Long exhibitId,
        String exhibitTitle
) {
}
