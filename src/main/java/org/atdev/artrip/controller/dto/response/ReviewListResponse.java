package org.atdev.artrip.controller.dto.response;

import lombok.*;
import org.atdev.artrip.domain.review.Review;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ReviewListResponse(
        Long reviewId,
        String reviewTitle,
        String content,
        LocalDate visitDate,
        LocalDateTime createdAt
) {

    public static ReviewListResponse from(Review review) {
        return ReviewListResponse.builder()
                .reviewId(review.getReviewId())
                .reviewTitle(review.getExhibit().getTitle())
                .content(review.getContent())
                .visitDate(review.getVisitDate())
                .createdAt(review.getCreatedAt())
                .build();
    }
}