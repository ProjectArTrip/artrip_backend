package org.atdev.artrip.domain.review.converter;

import org.atdev.artrip.domain.review.data.ReviewImage;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.review.data.Review;
import org.atdev.artrip.domain.review.web.dto.request.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.response.*;
import org.atdev.artrip.domain.review.web.dto.request.ReviewUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewConverter {

    private static String createSummary(Review review, int length) {
        if (review.getContent() == null || review.getContent().isEmpty()) return null;
        return review.getContent().length() > length
                ? review.getContent().substring(0, length) + "..."
                : review.getContent();
    }

    private static String createThumbnail(Review review) {
        if (review.getImages() == null || review.getImages().isEmpty()) {
            return null;
        }
        return review.getImages().get(0).getImageUrl();
    }


    // ----------------------------------------------------------------

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

    public ReviewResponse toReviewResponse(Review review) {

        User user = review.getUser();
        List<ReviewImageResponse> imageList = review.getImages() != null ?
                review.getImages().stream()
                        .map(img->new ReviewImageResponse(img.getImageId(),img.getImageUrl()))
                        .toList() :
                List.of();

        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .visitDate(review.getVisitDate())
                .images(imageList)
                .createdAt(review.getCreatedAt())
                .nickName(user.getNickName())
                .build();
    }


    public void updateReviewFromDto(Review review, ReviewUpdateRequest request) {

        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        if (request.getDate() != null) {
            review.setVisitDate(request.getDate());
        }
    }

    public static ReviewListResponse toSummary(Review review){

        return ReviewListResponse.builder()
                .reviewId(review.getReviewId())
                .reviewTitle(review.getExhibit().getTitle())
                .content(createSummary(review,20))
                .thumbnailUrl(createThumbnail(review))
                .visitDate(review.getVisitDate())
                .createdAt(review.getCreatedAt())
                .build();
    }


    public static ReviewExhibitResponse toExhibitReviewSummary(Review review){

        User user = review.getUser();

        return ReviewExhibitResponse.builder()
                .reviewId(review.getReviewId())
                .content(createSummary(review,20))
                .thumbnailUrl(createThumbnail(review))
                .visitDate(review.getVisitDate())
                .Nickname(user.getNickName())
                .build();
    }

//    public static List<ReviewListResponse> toSummaryList(List<Review> reviews) {
//        return reviews.stream()
//                .map(ReviewConverter::toSummary)
//                .toList();
//    }
//
//    public static ReviewSliceResponse toReviewListResponse(
//            List<Review> reviews,
//            Long nextCursor,
//            boolean hasNext
//    ) {
//        return ReviewSliceResponse.builder()
//                .reviews(toSummaryList(reviews))  // 여기서 리스트 변환
//                .nextCursor(nextCursor)
//                .hasNext(hasNext)
//                .build();
//    }

}
