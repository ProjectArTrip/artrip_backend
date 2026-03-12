package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.review.Review;

import java.time.LocalDate;
import java.util.List;

public record ReviewResult(
        Long reviewId,
        String content,
        LocalDate date,
        List<ReviewImageResult> images
) {
    public static ReviewResult from(Review review) {

        List<ReviewImageResult> images = review.getImages()
                .stream()
                .map(ReviewImageResult::from)
                .toList();

        return new ReviewResult(
                review.getReviewId(),
                review.getContent(),
                review.getVisitDate(),
                images
        );
    }
}
