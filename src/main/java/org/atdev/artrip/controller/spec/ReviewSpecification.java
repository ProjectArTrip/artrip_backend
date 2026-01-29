package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.ReviewCreateRequest;
import org.atdev.artrip.controller.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.controller.dto.response.ReviewResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewSpecification {


    @Operation(summary = "리뷰 생성")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_FOUND}
    )
    public ResponseEntity<Void> CreateReview(@PathVariable Long exhibitId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @RequestPart(value = "request") ReviewCreateRequest request,
                                             @LoginUser Long userId);

    @Operation(summary = "리뷰 수정")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            review = {ReviewErrorCode._REVIEW_USER_NOT_FOUND, ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    public ResponseEntity<Void> UpdateReview(@PathVariable Long reviewId,
                                                                       @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                                                       @RequestPart("request") ReviewUpdateRequest request,
                                                                       @LoginUser Long userId );
}
