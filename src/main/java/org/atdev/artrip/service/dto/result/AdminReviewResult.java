package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.domain.review.ReviewImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AdminReviewResult(
        Long reviewId,
        ReviewStatus status,
        String rejectionReason,
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
    public AdminReviewResult {
        imageUrls = imageUrls == null ? List.of() : imageUrls;
    }

    public AdminReviewResult(
            Long reviewId,
            ReviewStatus status,
            String rejectionReason,
            LocalDateTime rejectedAt,
            Long userId,
            String nickname,
            Long exhibitId,
            String exhibitTitle,
            String content,
            LocalDate visitDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean stampIssued
    ) {
        this(reviewId,
                status,
                rejectionReason,
                rejectedAt,
                userId,
                nickname,
                exhibitId,
                exhibitTitle,
                content,
                visitDate,
                createdAt,
                updatedAt,
                stampIssued,
                List.of());
    }

    public static AdminReviewResult from(Review review) {
        return new AdminReviewResult(
                review.getReviewId(),
                review.getStatus(),
                review.getRejectionReason(),
                review.getRejectedAt(),
                review.getUser().getUserId(),
                review.getUser().getNickName(),
                review.getExhibit().getExhibitId(),
                review.getExhibit().getTitle(),
                review.getContent(),
                review.getVisitDate(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                !review.getStamps().isEmpty(),
                review.getImages().stream().map(ReviewImage::getImageUrl).toList()
        );
    }
}
