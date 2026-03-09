package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.review.ReviewImage;

public record ReviewImageResult(
        Long reviewImageId,
        String imageUrl
) {
    public static ReviewImageResult from(ReviewImage image){
        return new ReviewImageResult(
                image.getImageId(),
                image.getImageUrl()
        );
    }
}