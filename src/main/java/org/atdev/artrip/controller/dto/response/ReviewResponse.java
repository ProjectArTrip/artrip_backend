package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.service.dto.result.ReviewImageResult;
import org.atdev.artrip.service.dto.result.ReviewResult;

import java.time.LocalDate;
import java.util.List;

public record ReviewResponse(
        Long reviewId,
        String content,
        LocalDate date,
        List<ReviewImageResult> images
) {
    public static ReviewResponse from(ReviewResult result){
        return new ReviewResponse(
                result.reviewId(),
                result.content(),
                result.date(),
                result.images()
        );
    }
}
