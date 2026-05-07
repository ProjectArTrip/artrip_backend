package org.atdev.artrip.service.dto.command;

public record AdminReviewRejectCommand(
        Long adminId,
        Long reviewId,
        String rejectionReason
) {
}
