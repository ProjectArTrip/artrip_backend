package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.FileFolder;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.domain.review.ReviewImage;
import org.atdev.artrip.converter.ReviewConverter;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.global.apipayload.code.status.S3ErrorCode;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.atdev.artrip.service.dto.result.ExhibitReviewResult;
import org.atdev.artrip.service.dto.result.MyReviewResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    private final ReviewLogicService reviewLogicService;

    public void createReview(ReviewCreateCommand command){

        if (command.images() != null && command.images().size() > 4) {
            throw new GeneralException(ReviewErrorCode._TOO_MANY_REVIEW_IMAGES);
        }

        List<String> s3Urls = new ArrayList<>();
        if (command.images() != null && !command.images().isEmpty()) {
            s3Urls = s3Service.uploadFiles(command.images(), FileFolder.REVIEWS);
        }

        try {
            reviewLogicService.saveReviewWithImages(command, s3Urls);
        } catch (Exception e) {
            s3Service.delete(s3Urls);
            throw e;
        }
    }

    public void updateReview(ReviewUpdateCommand command) {

        List<String> urlsToDelete = reviewLogicService.getUrlsToDelete(command.reviewId(), command.deleteImageIds());

        List<String> newS3Urls = (command.images() != null && !command.images().isEmpty())
                ? s3Service.uploadFiles(command.images(), FileFolder.REVIEWS)
                : new ArrayList<>();

        try {
            reviewLogicService.updateReviewData(command, newS3Urls);

            s3Service.delete(urlsToDelete);
        } catch (Exception e) {
            s3Service.delete(newS3Urls);
            throw e;
        }

    }


    public void deleteReview(Long reviewId,Long userId){

        List<String> s3Urls = reviewLogicService.deleteAndGetUrls(reviewId,userId);

        if (!s3Urls.isEmpty()) {
            try {
                s3Service.delete(s3Urls);
            } catch (Exception e) {
                throw new GeneralException(S3ErrorCode._IO_EXCEPTION_DELETE_FILE,e);
            }
        }
    }

    @Transactional
    public MyReviewResult getAllReview(Long userId, Long cursor, int size){

        Slice<Review> slice;
        if (cursor == null) {
            slice = reviewRepository.findTopByUserId(userId, PageRequest.ofSize(size));
        } else {
            slice = reviewRepository.findByUserIdAndIdLessThan(userId, cursor, PageRequest.ofSize(size));
        }

        long reviewTotalCount = reviewRepository.countByUserUserId(userId);

        return MyReviewResult.of(slice,reviewTotalCount);
    }

    @Transactional
    public ExhibitReviewResult getExhibitReview(Long exhibitId, Long cursor, int size){

        long exhibitTotalCount = reviewRepository.countByExhibit_ExhibitId(exhibitId);

        Slice<Review> slice;

        if (cursor == null) {
            slice = reviewRepository.findByExhibitId(exhibitId, PageRequest.ofSize(size));
        } else {
            slice = reviewRepository.findByExhibitIdAndIdLessThan(exhibitId, cursor, PageRequest.ofSize(size));
        }

//        List<ReviewExhibitResponse> summaries = slice.getContent()
//                .stream()
//                .map(ReviewConverter::toExhibitReviewSummary)
//                .toList();
        return ExhibitReviewResult.of(slice,exhibitTotalCount);
    }
}