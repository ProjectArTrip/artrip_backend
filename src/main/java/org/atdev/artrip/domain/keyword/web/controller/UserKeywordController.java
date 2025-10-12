package org.atdev.artrip.domain.keyword.web.controller;

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


    @GetMapping("/allkeywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.onSuccess(keywords));
    }

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<KeywordResponse> keywords = keywordService.getUserKeywords(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(keywords));
    }

    @PostMapping("/keywords")
    public ResponseEntity<ApiResponse<Void>> saveUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody KeywordRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername()); // subject → userId형변환

        keywordService.saveUserKeywords(userId, request.getKeywordIds());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


}

