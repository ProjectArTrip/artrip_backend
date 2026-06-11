package org.atdev.artrip.controller.dto.response;

import lombok.*;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.domain.review.ReviewImage;
import org.atdev.artrip.utils.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MyReviewResponse(
        Long reviewId,
        String reviewTitle,
        String content,
        List<String> photoUrls,
        String posterUrl,
        String hallName,
        LocalDate visitDate,
        LocalDateTime createdAt
) {

    public static MyReviewResponse from(Review review) {
        return MyReviewResponse.builder()
                .reviewId(review.getReviewId())
                .reviewTitle(review.getExhibit().getTitle())
                .content(review.getContent())
                .photoUrls(review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .map(StringUtils::emptyIfNull)
                        .toList())
                .posterUrl(review.getExhibit().getPosterUrl())
                .hallName(review.getExhibit().getExhibitHall().getName())
                .visitDate(review.getVisitDate())
                .createdAt(review.getCreatedAt())
                .build();
    }
}