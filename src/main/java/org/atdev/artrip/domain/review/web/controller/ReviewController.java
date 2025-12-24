package org.atdev.artrip.domain.review.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.service.ReviewService;
import org.atdev.artrip.domain.review.web.dto.request.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.response.ExhibitReviewSliceResponse;
import org.atdev.artrip.domain.review.web.dto.response.ReviewResponse;
import org.atdev.artrip.domain.review.web.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.domain.review.web.dto.response.ReviewSliceResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.ReviewError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            review = {ReviewError._REVIEW_USER_NOT_FOUND}
    )
    @PostMapping("/{exhibitId}")
    public ResponseEntity<CommonResponse<ReviewResponse>> CreateReview(@PathVariable Long exhibitId,
                                                                       @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                                       @RequestPart(value = "request") ReviewCreateRequest request,
                                                                       @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.createReview(exhibitId, request, images, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(review));
    }

    @Operation(summary = "리뷰 수정")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            review = {ReviewError._REVIEW_USER_NOT_FOUND, ReviewError._REVIEW_NOT_FOUND}
    )
    @PutMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewResponse>> UpdateReview(@PathVariable Long reviewId,
                                                                       @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                                       @RequestPart("request") ReviewUpdateRequest request,
                                                                       @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.updateReview(reviewId, request, images, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(review));
    }

    @Operation(summary = "리뷰 삭제")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            review = {ReviewError._REVIEW_USER_NOT_FOUND, ReviewError._REVIEW_NOT_FOUND}
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<String>> DeleteReview(@PathVariable Long reviewId,
                                                               @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess("리뷰 삭제 완료"));
    }

    @Operation(summary = "나의 리뷰 전체 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            review = {ReviewError._REVIEW_USER_NOT_FOUND}
    )
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<ReviewSliceResponse>> getAllReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewSliceResponse response = reviewService.getAllReview(userId, cursor, size);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Operation(summary = "전시 상세페이지 리뷰 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            review = {ReviewError._REVIEW_NOT_FOUND}
    )
    @GetMapping("/{exhibitId}/detail")
    public ResponseEntity<CommonResponse<ExhibitReviewSliceResponse>> getExhibitReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long exhibitId) {

        ExhibitReviewSliceResponse response = reviewService.getExhibitReview(exhibitId, cursor, size);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
