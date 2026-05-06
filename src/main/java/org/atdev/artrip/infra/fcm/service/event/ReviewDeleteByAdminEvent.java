package org.atdev.artrip.infra.fcm.service.event;

public record ReviewDeleteByAdminEvent(
        Long reviewId,
        Long userId,
        String exhibitTitle
) {
}
