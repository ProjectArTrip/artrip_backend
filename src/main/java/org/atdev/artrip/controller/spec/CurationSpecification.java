package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.constants.Country;
import org.atdev.artrip.controller.dto.response.CurationDetailCursorResponse;
import org.atdev.artrip.controller.dto.response.CurationSummaryListResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.utils.CursorPagination;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface CurationSpecification {

    @Operation(
            summary = "큐레이션 목록 조회",
            description = "국가별 큐레이션 목록 + 전시 카드 조회, country **전체 또는 빈값** -> 전체조회"
    )
    ResponseEntity<CurationSummaryListResponse> getSummary(
            @LoginUser Long userId,
            @RequestParam(required = false) Country country);
    @Operation(
            summary = "큐레이션 상세 조회",
            description = "큐레이션 목록에서 클릭 시 상세 페이지로 이동"
    )
    ResponseEntity<CurationDetailCursorResponse> getDetail(
            @LoginUser Long userId,
            @PathVariable Long curationId,
            @ParameterObject CursorPagination cursorPagination);

}
