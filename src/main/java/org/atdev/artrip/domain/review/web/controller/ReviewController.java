package org.atdev.artrip.domain.review.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.service.ReviewService;
import org.atdev.artrip.domain.review.web.dto.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.ReviewResponse;
import org.atdev.artrip.domain.review.web.dto.ReviewUpdateRequest;
import org.atdev.artrip.global.apipayload.ApiResponse;
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
    public ResponseEntity<ApiResponse<ReviewResponse>> CreateReview(@PathVariable Long exhibitId,
                                                                    @RequestPart("images") List<MultipartFile> images,
                                                                    @RequestPart("request") ReviewCreateRequest request,
                                                                    @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.createReview(exhibitId, request, images, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(review));
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> UpdateReview(@PathVariable Long reviewId,
                                                                    @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                                    @RequestPart("request") ReviewUpdateRequest request,
                                                                    @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.updateReview(reviewId, request, images, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(review));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> DeleteReview(@PathVariable Long reviewId,
                                                            @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess("리뷰 삭제 완료"));
    }

}
