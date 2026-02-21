package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.response.FavoriteListResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface FavoriteSpecification {

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = """
                    사용자 즐겨찾기 목록 무한 스크롤 조회
                    
                    **정렬 옵션**
                    - LATEST : 최신순
                    - ENDING_SOON: 마감순
                    
                    **필터옵션**
                    """
    )
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            user =  {UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @Valid FavoriteSearchCondition condition,
            @Valid CursorPagination cursorPagination,
            @LoginUser Long userId);
}
