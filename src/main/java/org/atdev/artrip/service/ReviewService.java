package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.FileFolder;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.domain.review.ReviewImage;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.converter.ReviewConverter;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.repository.ReviewImageRepository;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.controller.dto.request.ReviewCreateRequest;
import org.atdev.artrip.controller.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
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
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        Review review = reviewConverter.toEntity(user,exhibit,request);
        reviewRepository.save(review);

        List<String> s3Urls = (images == null || images.isEmpty())
                ? new ArrayList<>()
                : s3Service.uploadFiles(images, FileFolder.REVIEWS);

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
                .orElseThrow(()-> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)){
            throw new GeneralException(ReviewErrorCode._REVIEW_USER_NOT_FOUND);
        }

        reviewConverter.updateReviewFromDto(review, request);

        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {

            List<ReviewImage> preImages = reviewImageRepository.findByReview_ReviewId(reviewId);

            List<ReviewImage> imagesToDelete = preImages.stream()
                    .filter(img -> request.getDeleteImageIds().contains(img.getImageId()))
                    .toList();

            if (!imagesToDelete.isEmpty()) {
                List<String> urlsToDelete = imagesToDelete.stream()
                        .map(ReviewImage::getImageUrl)
                        .toList();

                s3Service.delete(urlsToDelete);

                review.getImages().removeAll(imagesToDelete);
            }
        }

        if (images != null && !images.isEmpty()) {
            List<String> s3Urls = s3Service.uploadFiles(images,FileFolder.POSTERS);
            List<ReviewImage> newReviewImages = reviewConverter.toReviewImage(review, s3Urls);
            reviewImageRepository.saveAll(newReviewImages);
            review.getImages().addAll(newReviewImages);
        }

        return reviewConverter.toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId,Long userId){


        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new GeneralException(ReviewErrorCode._REVIEW_NOT_FOUND));

        if (!review.getUser().getUserId().equals(userId)){
            throw new GeneralException(ReviewErrorCode._REVIEW_USER_NOT_FOUND);
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


        return new ReviewSliceResponse(summaries, nextCursor, slice.hasNext());
    }

    @Transactional
    public ExhibitReviewSliceResponse getExhibitReview(Long exhibitId, Long cursor, int size, ImageResizeRequest resize){

        long totalCount = reviewRepository.countByExhibit_ExhibitId(exhibitId);

        Slice<Review> slice;

        if (cursor == null) {
            slice = reviewRepository.findByExhibitId(exhibitId, PageRequest.ofSize(size));
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



        return new ExhibitReviewSliceResponse(summaries, nextCursor, slice.hasNext(),totalCount);
    }
}