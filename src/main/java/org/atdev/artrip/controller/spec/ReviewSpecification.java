package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.ReviewCreateRequest;
import org.atdev.artrip.controller.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.controller.dto.response.ExhibitReviewSliceResponse;
import org.atdev.artrip.controller.dto.response.ReviewResponse;
import org.atdev.artrip.controller.dto.response.ReviewSliceResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewSpecification {


    @Operation(summary = "리뷰 생성")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE}
    )
    public ResponseEntity<Void> createReview(@PathVariable Long exhibitId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @RequestPart(value = "request") ReviewCreateRequest request,
                                             @LoginUser Long userId);

    @Operation(summary = "리뷰 수정")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE, ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                                                       @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                                       @RequestPart("request") ReviewUpdateRequest request,
                                                                       @LoginUser Long userId );


    @Operation(summary = "리뷰 삭제")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE, ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @LoginUser Long userId);


    @Operation(summary = "나의 리뷰 전체 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_ROLE}
    )
    public ResponseEntity<CommonResponse<ReviewSliceResponse>> getAllMyReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId);


    @Operation(summary = "전시 상세페이지 리뷰 조회 (무한스크롤)")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<ExhibitReviewSliceResponse>> getExhibitReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long exhibitId);
}
