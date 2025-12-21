package org.atdev.artrip.domain.user.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.service.KeywordService;
import org.atdev.artrip.domain.keyword.web.dto.KeywordRequest;
import org.atdev.artrip.domain.user.service.UserService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.KeywordError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "나의 취향 분석", description = "내가 선택한 키워드 선택 저장")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            keyword = {KeywordError._KEYWORD_INVALID_REQUEST, KeywordError._KEYWORD_SELECTION_LIMIT_EXCEEDED, KeywordError._KEYWORD_NOT_FOUND}
    )
    @PostMapping("/keywords")
    public ResponseEntity<CommonResponse<Void>> saveUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody KeywordRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername()); // subject → userId형변환

        userService.saveUserKeywords(userId, request.getKeywordIds());
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }


}
