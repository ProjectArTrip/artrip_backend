package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.response.SearchHistoryListResponse;
import org.atdev.artrip.controller.dto.response.SearchHistoryResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.SearchErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface SearchHistorySpecification {

    @Operation(summary = "최근 검색어 TOP10 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND}
    )
    ResponseEntity<SearchHistoryListResponse> getRecentSearchHistory(@LoginUser Long userId);

    @Operation(summary = "id별 검색어 삭제")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            search = {SearchErrorCode._SEARCH_HISTORY_NOT_FOUND, SearchErrorCode._SEARCH_HISTORY_DELETE_FORBIDDEN}
    )
    ResponseEntity<Void> deleteSearchHistory(@LoginUser Long userId, @PathVariable Long searchHistoryId);

}
