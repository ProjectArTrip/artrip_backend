package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.controller.dto.response.ExhibitSearchResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.SearchError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.SearchService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
@Tag(name = "Search-controller", description = "전시회 검색 및 검색어 관리 API")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "전시회 검색", description = "키워드로 전시회를 검색합니다.")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._BAD_REQUEST},
            search = {SearchError._SEARCH_EXHIBIT_NOT_FOUND, SearchError._SEARCH_KEYWORD_INVALID}
    )
    @GetMapping("/exhibits")
    public CommonResponse<FilterResponse<ExhibitSearchResponse>> searchExhibits(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Long size,
            @ParameterObject ImageResizeRequest resize,
            @AuthenticationPrincipal UserDetails userDetails){

        Long userId = (userDetails != null) ? Long.parseLong(userDetails.getUsername()) : null;

        FilterResponse<ExhibitSearchResponse> result = searchService.getKeyword(keyword, cursor, size, userId, resize);

        return CommonResponse.onSuccess(result);
    }
}
