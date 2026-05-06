package org.atdev.artrip.infra.fcm.service.event;

import org.atdev.artrip.constants.ReviewRejectionReason;

public record ReviewRejectedEvent(
        Long reviewId,
        Long userId,
        ReviewRejectionReason reason
) {
}
