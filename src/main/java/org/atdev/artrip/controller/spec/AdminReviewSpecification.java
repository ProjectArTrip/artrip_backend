package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.AdminReviewRejectRequest;
import org.atdev.artrip.controller.dto.request.AdminSearchReviewRequest;
import org.atdev.artrip.controller.dto.response.AdminReviewPageResponse;
import org.atdev.artrip.controller.dto.response.AdminReviewResponse;
import org.atdev.artrip.global.apipayload.code.error.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.error.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.utils.page.Criteria;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminReviewSpecification {

    @Operation(summary = "관리자 리뷰 목록 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<AdminReviewPageResponse> list(
            @LoginUser Long adminId,
            AdminSearchReviewRequest request,
            @ParameterObject Criteria criteria
    );


    @Operation(summary = "관리자 리뷰 단건 상세")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            review = {ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    ResponseEntity<AdminReviewResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    );

    @Operation(summary = "관리자 리뷰 승인 (PENDING → APPROVED)")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            review = {
                    ReviewErrorCode._REVIEW_NOT_FOUND,
                    ReviewErrorCode._REVIEW_ALREADY_APPROVED,
                    ReviewErrorCode._REVIEW_ALREADY_REJECTED,
                    ReviewErrorCode._REVIEW_NOT_PENDING
            }
    )
    ResponseEntity<Void> approve(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    );

    @Operation(summary = "관리자 리뷰 반려 (PENDING → REJECTED, 사유 포함)")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED, CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            review = {
                    ReviewErrorCode._REVIEW_NOT_FOUND,
                    ReviewErrorCode._REVIEW_ALREADY_REJECTED,
                    ReviewErrorCode._REVIEW_ALREADY_APPROVED,
                    ReviewErrorCode._REVIEW_NOT_PENDING
            }
    )
    ResponseEntity<Void> reject(
            @LoginUser Long adminId,
            @PathVariable Long reviewId,
            @Valid @RequestBody AdminReviewRejectRequest request
    );

    @Operation(summary = "관리자 리뷰 삭제 (사용자에게 삭제 알림 발송)")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            review = {ReviewErrorCode._REVIEW_NOT_FOUND}
    )
    ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    );
}
