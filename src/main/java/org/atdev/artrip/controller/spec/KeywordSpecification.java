package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.KeywordRequest;
import org.atdev.artrip.controller.dto.response.KeywordListResponse;
import org.atdev.artrip.controller.dto.response.KeywordResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.KeywordErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface KeywordSpecification {

    @Operation(summary = "나의 취향 분석", description = "내가 선택한 키워드 선택 저장")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            keyword = {KeywordErrorCode._KEYWORD_INVALID_REQUEST, KeywordErrorCode._KEYWORD_SELECTION_LIMIT_EXCEEDED, KeywordErrorCode._KEYWORD_NOT_FOUND}
    )
    public ResponseEntity<Void> saveKeywords( @LoginUser Long userId,
                                                                  @RequestBody KeywordRequest request);

    @Operation(summary = "모든 키워드 조회", description = "전체 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR},
            keyword = {KeywordErrorCode._KEYWORD_INVALID_REQUEST}
    )
    public ResponseEntity<KeywordListResponse> getAllKeywords();

    @Operation(summary = "나의 키워드 조회", description = "내가 선택한 키워드 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            keyword = {KeywordErrorCode._KEYWORD_INVALID_REQUEST}
    )
    public ResponseEntity<KeywordListResponse> getKeyword(
            @LoginUser Long userId);
}
