package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.FavoriteRequest;
import org.atdev.artrip.controller.dto.response.FavoriteListResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

public interface FavoriteSpecification {

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = """
                    사용자 즐겨찾기 목록 무한 스크롤 조회
                    
                    **정렬 옵션 **
                    - LATEST : 최신순
                    - ENDING_SOON: 마감순
                    - 빈값일 경우 전체 조회 (string 안됩니다.)
                    
                    **필터옵션**
                    - isDomestic=true : 국내전시
                    - isDomestic=false : 해외전시
                    - isDomestic: 빈값일 경우 전체 조회
                    """
    )
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            user =  {UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @ModelAttribute FavoriteRequest request,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Long size,
            @LoginUser Long userId);
}
