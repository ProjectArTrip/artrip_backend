package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.response.FavoriteCursorResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;

public interface FavoriteSpecification {

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = """
                    사용자 즐겨찾기 목록 무한 스크롤 조회
                    
                    **정렬 옵션**
                    - LATEST : 최신순
                    - ENDING_SOON: 마감순
                    """
    )
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            user =  {UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<FavoriteCursorResponse> getFavorites(
            @Valid FavoriteSearchCondition condition,
            @Valid CursorPagination cursorPagination,
            @LoginUser Long userId);

    @Operation(
            summary = "즐겨찾기 추가"
    )
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND},
            favorite = {FavoriteErrorCode._FAVORITE_ALREADY_EXISTS}
    )
    public ResponseEntity<Void> createFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId
    );

    @Operation(summary = "즐겨찾기 제거")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND},
            favorite = {FavoriteErrorCode._FAVORITE_NOT_FOUND}
    )
    public ResponseEntity<Void> removeFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId
    );
}
