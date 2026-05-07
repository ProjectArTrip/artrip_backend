package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.ReviewRejectionReason;

public record AdminReviewRejectCommand(
        Long adminId,
        Long reviewId,
        ReviewRejectionReason rejectionReason
) {
}
