package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.ReviewRejectionReason;
import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.service.dto.result.AdminReviewResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AdminReviewResponse(
        Long reviewId,
        ReviewStatus status,
        ReviewRejectionReason rejectionReason,
        LocalDateTime rejectedAt,
        Long userId,
        String nickname,
        Long exhibitId,
        String exhibitTitle,
        String content,
        LocalDate visitDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean stampIssued,
        List<String> imageUrls
) {

    public static AdminReviewResponse from (AdminReviewResult result) {
        return new AdminReviewResponse(
                result.reviewId(),
                result.status(),
                result.rejectionReason(),
                result.rejectedAt(),
                result.userId(),
                result.nickname(),
                result.exhibitId(),
                result.exhibitTitle(),
                result.content(),
                result.visitDate(),
                result.createdAt(),
                result.updatedAt(),
                result.stampIssued(),
                result.imageUrls()
        );
    }
}
