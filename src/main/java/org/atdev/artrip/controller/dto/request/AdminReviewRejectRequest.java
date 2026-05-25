package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.atdev.artrip.service.dto.command.AdminReviewRejectCommand;

public record AdminReviewRejectRequest(
        @NotBlank @Size(max = 200) String rejectionReason
) {
    public AdminReviewRejectCommand toCommand(Long adminId, Long reviewId) {
        return new AdminReviewRejectCommand(adminId, reviewId, rejectionReason);
    }
}
