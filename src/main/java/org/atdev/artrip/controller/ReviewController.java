package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.spec.ReviewSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.ReviewService;
import org.atdev.artrip.controller.dto.request.ReviewCreateRequest;
import org.atdev.artrip.controller.dto.response.ExhibitReviewSliceResponse;
import org.atdev.artrip.controller.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.controller.dto.response.ReviewSliceResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController implements ReviewSpecification {

    private final ReviewService reviewService;

    @PostMapping("/{exhibitId}")
    public ResponseEntity<Void> CreateReview(@PathVariable Long exhibitId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @RequestPart(value = "request") ReviewCreateRequest request,
                                             @LoginUser Long userId){

        ReviewCreateCommand command = ReviewCreateRequest.toCommand(request.date(), request.content(),exhibitId, userId,images);
        reviewService.createReview(command);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> UpdateReview(@PathVariable Long reviewId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @RequestPart("request") ReviewUpdateRequest request,
                                             @LoginUser Long userId ){

        ReviewUpdateCommand command = ReviewUpdateRequest.toCommand(request.date(), request.content(),request.deleteImageIds(),reviewId, userId,images);
        reviewService.updateReview(command);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "리뷰 삭제")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE, ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<String>> DeleteReview(@PathVariable Long reviewId,
                                                               @LoginUser Long userId){

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.ok(CommonResponse.onSuccess("리뷰 삭제 완료"));
    }

    @Operation(summary = "나의 리뷰 전체 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE}
    )
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<ReviewSliceResponse>> getAllReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId,
            @ParameterObject ImageResizeRequest resize) {

        ReviewSliceResponse response = reviewService.getAllReview(userId, cursor, size, resize);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Operation(summary = "전시 상세페이지 리뷰 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    @GetMapping("/{exhibitId}/detail")
    public ResponseEntity<CommonResponse<ExhibitReviewSliceResponse>> getExhibitReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long exhibitId,
            @ParameterObject ImageResizeRequest resize) {

        ExhibitReviewSliceResponse response = reviewService.getExhibitReview(exhibitId, cursor, size, resize);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
