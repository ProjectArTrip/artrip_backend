package org.atdev.artrip.domain.keyword.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.service.KeywordService;
import org.atdev.artrip.domain.keyword.web.dto.KeywordRequest;
import org.atdev.artrip.domain.keyword.web.dto.KeywordResponse;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserKeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "모든 키워드 조회", description = "전체 조회")
    @GetMapping("/allkeywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.onSuccess(keywords));
    }

    @Operation(summary = "나의 키워드 조회", description = "내가 선택한 키워드 조회")
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<KeywordResponse> keywords = keywordService.getUserKeywords(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(keywords));
    }

    @Operation(summary = "내 키워드 선택 저장", description = "내가 선택한 키워드 저장")
    @PostMapping("/keywords")
    public ResponseEntity<ApiResponse<Void>> saveUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody KeywordRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername()); // subject → userId형변환

        keywordService.saveUserKeywords(userId, request.getKeywordIds());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


}

