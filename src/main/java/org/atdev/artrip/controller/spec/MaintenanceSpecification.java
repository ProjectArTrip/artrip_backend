package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.response.MaintenanceStatusResponse;
import org.springframework.http.ResponseEntity;

public interface MaintenanceSpecification {

    @Operation(
            summary = "서버 점검 상태 조회"
    )
    ResponseEntity<MaintenanceStatusResponse> getMaintenanceStatus();
}
