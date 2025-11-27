package org.atdev.artrip.domain.search.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.search.service.SearchHistoryService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.domain.search.response.ExhibitSearchResponse;
import org.atdev.artrip.domain.search.service.ExhibitSearchService;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.SearchError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
@Tag(name = "Search-controller", description = "전시회 검색 및 검색어 관리 API")
public class SearchController {

    private final ExhibitSearchService exhibitSearchService;
    private final SearchHistoryService searchHistoryService;

    @Operation(summary = "전시회 검색", description = "키워드로 전시회를 검색합니다.")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._BAD_REQUEST},
            search = {SearchError._SEARCH_EXHIBIT_NOT_FOUND, SearchError._SEARCH_KEYWORD_INVALID}
    )
    @GetMapping("/exhibits")
    public CommonResponse<List<ExhibitSearchResponse>> searchExhibits(@RequestParam String keyword
    , @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<ExhibitSearchResponse> results = exhibitSearchService.keywordSearch(keyword, userId);
        return CommonResponse.onSuccess(results);
    }

    @Operation(summary = "최근 검색어 조회", description = "사용자의 최근 검색어 10개를 조회합니다.")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._BAD_REQUEST},
            search = {SearchError._SEARCH_HISTORY_NOT_FOUND}
    )
    @GetMapping("/history")
    public CommonResponse<List<String>> getRecentKeywords(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<String> keywords = searchHistoryService.findRecent(userId);
        return CommonResponse.onSuccess(keywords);
    }

    @Operation(summary = "검색어 삭제", description = "사용자의 특정 검색어를 삭제합니다.")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._BAD_REQUEST},
            search = {SearchError._SEARCH_HISTORY_NOT_FOUND}
    )
    @DeleteMapping("/history")
    public CommonResponse<Void> deleteKeywords(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        searchHistoryService.remove(userId, keyword);
        return CommonResponse.onSuccess(null);
    }

    @Operation(summary = "검색어 전체 삭제", description = "사용자의 모든 검색어를 삭제합니다.")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._BAD_REQUEST},
            search = {SearchError._SEARCH_HISTORY_NOT_FOUND, SearchError._SEARCH_TOO_FREQUENT}
    )
    @DeleteMapping("/history/all")
    public CommonResponse<Void> deleteAllKeywords(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        searchHistoryService.removeAll(userId);

        return CommonResponse.onSuccess(null);
    }

    private Long extractUserId(UserDetails userDetails) {
        return userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
    }

}
