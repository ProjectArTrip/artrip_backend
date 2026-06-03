package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.AdminCreateExhibitRequest;
import org.atdev.artrip.controller.dto.request.AdminSearchExhibitRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateExhibitRequest;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.global.apipayload.code.error.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AdminExhibitSpecification {

    @Operation(summary = "관리자 전시 목록 조회",
            description = "검색 + 필터 페이징처리")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED, CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    ResponseEntity<PageResponse<AdminExhibitListItemResponse>> list(
            @LoginUser Long adminId,
            @ParameterObject AdminSearchExhibitRequest request,
            @ParameterObject PageQuery pageQuery
    );

    @Operation(summary = "관리자 전시 단건 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            exhibit = {ExhibitErrorCode._EXHIBIT_NOT_FOUND}
    )
    ResponseEntity<AdminExhibitResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId
    );

    @Operation(summary = "관리자 전시 단건 등록")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED, CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            exhibit = {ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE, ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND}
    )
    ResponseEntity<AdminExhibitCreateResponse> create(
            @LoginUser Long adminId,
            @Valid @RequestBody AdminCreateExhibitRequest request
    );

    @Operation(summary = "관리자 전시 CSV 일괄 등록")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            exhibit = {ExhibitErrorCode._CSV_EMPTY, ExhibitErrorCode._CSV_INVALID_FORMAT, ExhibitErrorCode._CSV_INVALID_ROW,
                    ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE, ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND
            }
    )
    ResponseEntity<AdminExhibitBulkCreateResponse> bulkCreateFromCsv(
            @LoginUser Long adminId,
            @RequestParam("file") MultipartFile file
    );


    @Operation(summary = "관리자 전시 수정")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED, CommonErrorCode._BAD_REQUEST},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            exhibit = {
                    ExhibitErrorCode._EXHIBIT_NOT_FOUND,
                    ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE,
                    ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND
            }
    )
    ResponseEntity<Void> update(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId,
            @Valid @RequestBody AdminUpdateExhibitRequest request
    );

    @Operation(summary = "관리자 전시 삭제")
    @ApiErrorResponses(
            common = {CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            exhibit = {ExhibitErrorCode._EXHIBIT_NOT_FOUND}
    )
    ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId
    );


}
