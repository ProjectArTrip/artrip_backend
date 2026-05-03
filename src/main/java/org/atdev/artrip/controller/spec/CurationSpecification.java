package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.CurationSearchRequest;
import org.atdev.artrip.controller.dto.response.CurationDetailCursorResponse;
import org.atdev.artrip.controller.dto.response.CurationSummaryResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.utils.CursorPagination;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface CurationSpecification {

    @Operation(
            summary = "큐레이션 랜덤 1개 조회",
            description =
                    """
                    domestic(false) , country 빈값 조회 시 전체 랜덤 1개 조회,
                    domestic(true) 전체 랜덤
                    """
    )
    ResponseEntity<CurationSummaryResponse> getSummary(
            @LoginUser Long userId,
            @ParameterObject CurationSearchRequest request);

    @Operation(
            summary = "큐레이션 상세 조회",
            description = "큐레이션 목록에서 클릭 시 상세 페이지로 이동"
    )
    ResponseEntity<CurationDetailCursorResponse> getDetail(
            @LoginUser Long userId,
            @PathVariable Long curationId,
            @ParameterObject CursorPagination cursorPagination);

}
