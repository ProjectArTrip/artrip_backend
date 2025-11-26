package org.atdev.artrip.domain.review.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.service.ReviewService;
import org.atdev.artrip.domain.review.web.dto.request.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.response.ReviewResponse;
import org.atdev.artrip.domain.review.web.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.domain.review.web.dto.response.ReviewSliceResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
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
    @PostMapping("/{exhibitId}")
    public ResponseEntity<CommonResponse<ReviewResponse>> CreateReview(@PathVariable Long exhibitId,
                                                                       @RequestPart("images") List<MultipartFile> images,
                                                                       @RequestPart("request") ReviewCreateRequest request,
                                                                       @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.createReview(exhibitId, request, images, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(review));
    }

    @Operation(summary = "리뷰 수정")
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
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<String>> DeleteReview(@PathVariable Long reviewId,
                                                               @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess("리뷰 삭제 완료"));
    }

    @Operation(summary = "나의 리뷰 전체 조회 (무한스크롤)")
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<ReviewSliceResponse>> getAllReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewSliceResponse response = reviewService.getAllReview(userId, cursor, size);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }


}
