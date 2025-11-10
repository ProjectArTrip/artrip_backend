package org.atdev.artrip.domain.review.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.service.ReviewService;
import org.atdev.artrip.domain.review.web.dto.ReviewCreateRequest;
import org.atdev.artrip.domain.review.web.dto.ReviewResponse;
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
    @PostMapping("/")
    public ResponseEntity<ApiResponse<ReviewResponse>> CreateReview(@RequestPart("images") List<MultipartFile> images,
                                                                    @RequestPart("request") ReviewCreateRequest request,
                                                                    @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.valueOf(userDetails.getUsername());

        ReviewResponse review = reviewService.createReview(request, images, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(review));
    }
}
