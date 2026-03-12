package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.domain.review.ReviewImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ExhibitReviewListResponse(
        Long reviewId,
        String reviewTitle,
        String content,
        String reviewer,
        List<String> photoUrls,
        LocalDate visitDate,
        LocalDateTime createdAt
) {
    public static ExhibitReviewListResponse from(Review review) {
        return ExhibitReviewListResponse.builder()
                .reviewId(review.getReviewId())
                .reviewTitle(review.getExhibit().getTitle())
                .content(review.getContent())
                .reviewer(review.getUser().getNickName())
                .photoUrls(review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList())
                .visitDate(review.getVisitDate())
                .createdAt(review.getCreatedAt())
                .build();
    }
}