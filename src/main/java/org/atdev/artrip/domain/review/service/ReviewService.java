package org.atdev.artrip.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.review.data.ReviewImage;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.review.converter.ReviewConverter;
import org.atdev.artrip.domain.review.data.Review;
import org.atdev.artrip.domain.review.repository.ReviewImageRepository;
import org.atdev.artrip.domain.review.repository.ReviewRepository;
import org.atdev.artrip.domain.review.web.dto.request.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.response.*;
import org.atdev.artrip.domain.review.web.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.global.apipayload.code.status.ReviewError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.global.s3.web.dto.request.ImageResizeRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;
    private final ReviewConverter reviewConverter;
    private final S3Service s3Service;


    @Transactional
    public ReviewResponse createReview(Long exhibitId, ReviewCreateRequest request, List<MultipartFile> images, Long userId){

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ReviewError._REVIEW_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ReviewError._REVIEW_NOT_FOUND));

        Review review = reviewConverter.toEntity(user,exhibit,request);
        reviewRepository.save(review);

        List<String> s3Urls = (images == null || images.isEmpty())
                ? new ArrayList<>()
                : s3Service.uploadReviews(images);

        List<ReviewImage> reviewImages = reviewConverter.toReviewImage(review,s3Urls);

        if (reviewImages!=null&&!reviewImages.isEmpty()){
            reviewImageRepository.saveAll(reviewImages);
            review.setImages(reviewImages);
        }

        return reviewConverter.toReviewResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, List<MultipartFile> images, Long userId){


        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new GeneralException(ReviewError._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)){
            throw new GeneralException(ReviewError._REVIEW_USER_NOT_FOUND);
        }

        reviewConverter.updateReviewFromDto(review, request);

        //이미지 삭제
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {

            List<ReviewImage> preImages = reviewImageRepository.findByReview_ReviewId(reviewId);

            log.info("리뷰아이디:{}: 기존 이미지{}", reviewId, preImages.stream()
                    .map(img -> img.getImageId() + ":" + img.getImageUrl())
                    .toList());
            log.info("삭제할 이미지 ID: {}", request.getDeleteImageIds());


            List<ReviewImage> imagesToDelete = preImages.stream()
                    .filter(img -> request.getDeleteImageIds().contains(img.getImageId()))
                    .toList();

            log.info("삭제될 이미지: {}", imagesToDelete.stream()
                    .map(img -> img.getImageId() + ":" + img.getImageUrl())
                    .toList());

            if (!imagesToDelete.isEmpty()) {
                List<String> urlsToDelete = imagesToDelete.stream()
                        .map(ReviewImage::getImageUrl)
                        .toList();

                s3Service.delete(urlsToDelete);

                review.getImages().removeAll(imagesToDelete);
            }
        }

        //이미지 추가
        if (images != null && !images.isEmpty()) {
            List<String> s3Urls = s3Service.uploadPoster(images);
            List<ReviewImage> newReviewImages = reviewConverter.toReviewImage(review, s3Urls);
            reviewImageRepository.saveAll(newReviewImages);
            review.getImages().addAll(newReviewImages);
        }

        return reviewConverter.toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId,Long userId){


        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new GeneralException(ReviewError._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)){
            throw new GeneralException(ReviewError._REVIEW_USER_NOT_FOUND);
        }


        List<String> s3Urls = review.getImages() != null ?
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList()
                : List.of();

        s3Service.delete(s3Urls);
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewSliceResponse getAllReview(Long userId, Long cursor, int size, ImageResizeRequest resize){

        Slice<Review> slice;

        if (cursor == null) {
            slice = reviewRepository.findTopByUserId(userId, PageRequest.ofSize(size));
        } else {
            slice = reviewRepository.findByUserIdAndIdLessThan(userId, cursor, PageRequest.ofSize(size));
        }

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getContent().size() - 1).getReviewId()
                : null;

        List<ReviewListResponse> summaries = slice.getContent()
                .stream()
                .map(ReviewConverter::toSummary)
                .toList();

        summaries.forEach(r -> r.setThumbnailUrl(
                s3Service.buildResizeUrl(r.getThumbnailUrl(), resize.getW(), resize.getH(), resize.getF())
        ));

        return new ReviewSliceResponse(summaries, nextCursor, slice.hasNext());
    }

    @Transactional
    public ExhibitReviewSliceResponse getExhibitReview(Long exhibitId, Long cursor, int size, ImageResizeRequest resize){

        long totalCount = reviewRepository.countByExhibit_ExhibitId(exhibitId);

        Slice<Review> slice;

        if (cursor == null) {
            slice = reviewRepository.findTopByExhibitId(exhibitId, PageRequest.ofSize(size));
        } else {
            slice = reviewRepository.findByExhibitIdAndIdLessThan(exhibitId, cursor, PageRequest.ofSize(size));
        }

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getContent().size() - 1).getReviewId()
                : null;

        List<ReviewExhibitResponse> summaries = slice.getContent()
                .stream()
                .map(ReviewConverter::toExhibitReviewSummary)
                .toList();

        summaries.forEach(r -> r.setThumbnailUrl(
                s3Service.buildResizeUrl(r.getThumbnailUrl(), resize.getW(), resize.getH(), resize.getF())
        ));

        return new ExhibitReviewSliceResponse(summaries, nextCursor, slice.hasNext(),totalCount);
    }
}