package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.KeywordService;
import org.atdev.artrip.controller.dto.request.KeywordRequest;
import org.atdev.artrip.controller.dto.response.KeywordResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.KeywordError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserKeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "나의 취향 분석", description = "내가 선택한 키워드 선택 저장")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            keyword = {KeywordError._KEYWORD_INVALID_REQUEST, KeywordError._KEYWORD_SELECTION_LIMIT_EXCEEDED, KeywordError._KEYWORD_NOT_FOUND}
    )
    @PostMapping("/keywords")
    public ResponseEntity<CommonResponse<Void>> saveUserKeywords(
            @LoginUser Long userId,
            @RequestBody KeywordRequest request) {

        keywordService.saveUserKeywords(userId, request.getKeywordIds());
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }

    @Operation(summary = "모든 키워드 조회", description = "전체 조회")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR},
            keyword = {KeywordError._KEYWORD_INVALID_REQUEST}
    )
    @GetMapping("/allkeywords")
    public ResponseEntity<CommonResponse<List<KeywordResponse>>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();
        return ResponseEntity.ok(CommonResponse.onSuccess(keywords));
    }

    @Operation(summary = "나의 키워드 조회", description = "내가 선택한 키워드 조회")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            keyword = {KeywordError._KEYWORD_INVALID_REQUEST}
    )
    @GetMapping("/keywords")
    public ResponseEntity<CommonResponse<List<KeywordResponse>>> getUserKeywords(
            @LoginUser Long userId) {

        List<KeywordResponse> keywords = keywordService.getUserKeywords(userId);
        return ResponseEntity.ok(CommonResponse.onSuccess(keywords));
    }


}

