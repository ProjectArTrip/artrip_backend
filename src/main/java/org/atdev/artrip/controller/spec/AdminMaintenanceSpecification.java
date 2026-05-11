package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.AdminUpsertMaintenanceRequest;
import org.atdev.artrip.controller.dto.response.MaintenanceStatusResponse;
import org.atdev.artrip.global.apipayload.code.error.MaintenanceErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminMaintenanceSpecification {

    @Operation(summary = "관리자 서버 점검 상태 조회")
    @ApiErrorResponses(user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN})
    ResponseEntity<MaintenanceStatusResponse> getAdminMaintenanceStatus(@LoginUser Long userId);

    @Operation(summary = "관리자 서버 점검 상태 등록 또는 수정",
    description = """
            **필수 입력 : state** : **정상**, **안내**, **차단**
            """)
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN},
            maintenance = {MaintenanceErrorCode._MAINTENANCE_INVALID_PERIOD}
    )
    ResponseEntity<MaintenanceStatusResponse> upsertMaintenance(
            @LoginUser Long userId,
            @Valid @RequestBody AdminUpsertMaintenanceRequest request
            );
}
