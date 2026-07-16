package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.AdminCreateCurationRequest;
import org.atdev.artrip.controller.dto.request.AdminCurationSearchRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateCurationRequest;
import org.atdev.artrip.controller.dto.response.AdminCurationCreateResponse;
import org.atdev.artrip.controller.dto.response.AdminCurationDetailResponse;
import org.atdev.artrip.controller.dto.response.AdminCurationListResponse;
import org.atdev.artrip.global.apipayload.code.error.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminCurationSpecification {

    @Operation(summary = "큐레이션 목록 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<PageResponse<AdminCurationListResponse>> list(
            @LoginUser Long adminId,
            AdminCurationSearchRequest request,
            PageQuery pageQuery
    );

    @Operation(summary = "큐레이션 상세 조회")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<AdminCurationDetailResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long curationId
    );

    @Operation(summary = "큐레이션 생성")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED, CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<AdminCurationCreateResponse> create(
            @LoginUser Long adminId,
            @RequestBody AdminCreateCurationRequest request
    );

    @Operation(summary = "큐레이션 수정")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<Void> update(
            @LoginUser Long adminId,
            @PathVariable Long curationId,
            @RequestBody AdminUpdateCurationRequest request
    );

    @Operation(summary = "큐레이션 삭제")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long curationId
    );

}
