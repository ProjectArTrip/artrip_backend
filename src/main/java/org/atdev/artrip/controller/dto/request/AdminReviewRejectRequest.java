package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import org.atdev.artrip.constants.ReviewRejectionReason;
import org.atdev.artrip.service.dto.command.AdminReviewRejectCommand;

public record AdminReviewRejectRequest(
        @NotNull String rejectionReason
) {
    public AdminReviewRejectCommand toCommand(Long adminId, Long reviewId) {
        return new AdminReviewRejectCommand(adminId, reviewId, ReviewRejectionReason.valueOf(rejectionReason));
    }
}
