package org.atdev.artrip.domain.review.converter;

import org.atdev.artrip.domain.review.data.ReviewImage;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.review.data.Review;
import org.atdev.artrip.domain.review.web.dto.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.ReviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewConverter {

    public Review toEntity(User user, Exhibit exhibit, ReviewCreateRequest request) {
        return Review.builder()
                .user(user)
                .exhibit(exhibit)
                .content(request.getContent())
                .visitDate(request.getDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<ReviewImage> toReviewImage(Review review, List<String> imageUrls) {
        List<ReviewImage> reviewImages = new ArrayList<>();

        int maxOrder = 0;
        for (ReviewImage img : review.getImages()) {
            if (img.getDisplayOrder() > maxOrder) {
                maxOrder = img.getDisplayOrder();
            }
        }
        int order = maxOrder + 1;

        for (String url : imageUrls) {
            reviewImages.add(ReviewImage.builder()
                    .review(review)
                    .imageUrl(url)
                    .displayOrder(order++)
                    .build());
        }
        return reviewImages;
    }

    public ReviewResponse toReviewResponse(Review review, Exhibit exhibit) {
        List<String> imageUrls = review.getImages() != null ?
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList() :
                List.of();

        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .exhibitId(exhibit.getExhibitId())
                .content(review.getContent())
                .visitDate(review.getVisitDate())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }

}
