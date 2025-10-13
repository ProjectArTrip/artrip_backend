package org.atdev.artrip.domain.search.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.search.service.SearchHistoryService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.atdev.artrip.domain.search.response.ExhibitSearchResponse;
import org.atdev.artrip.domain.search.service.ExhibitSearchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
public class SearchController {

    private final ExhibitSearchService exhibitSearchService;
    private final SearchHistoryService searchHistoryService;

    @Operation(summary = "전시회 검색", description = "키워드로 전시회를 검색합니다.")
    @GetMapping("/exhibits")
    public ApiResponse<List<ExhibitSearchResponse>> searchExhibits(@RequestParam String keyword
    , @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<ExhibitSearchResponse> results = exhibitSearchService.search(keyword, userId);
        return ApiResponse.onSuccess(results);
    }

    @Operation(summary = "최근 검색어 조회", description = "사용자의 최근 검색어 10개를 조회합니다.")
    @GetMapping("/history")
    public ApiResponse<List<String>> getRecentKeywords(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<String> keywords = searchHistoryService.findRecent(userId);
        return ApiResponse.onSuccess(keywords);
    }

    @Operation(summary = "검색어 삭제", description = "사용자의 특정 검색어를 삭제합니다.")
    @DeleteMapping("/history")
    public ApiResponse<Void> deleteKeywords(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        searchHistoryService.remove(userId, keyword);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "검색어 전체 삭제", description = "사용자의 모든 검색어를 삭제합니다.")
    @DeleteMapping("/history/all")
    public ApiResponse<Void> deleteAllKeywords(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        searchHistoryService.removeAll(userId);

        return ApiResponse.onSuccess(null);
    }

    private Long extractUserId(UserDetails userDetails) {
        return userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
    }

}
