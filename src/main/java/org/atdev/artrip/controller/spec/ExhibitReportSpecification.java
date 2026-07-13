package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.ExhibitReportRequest;
import org.atdev.artrip.global.resolver.LoginUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface ExhibitReportSpecification {

    @Operation(
            summary = "전시회 제안 요청"
    )
    ResponseEntity<Long> report(
            @LoginUser Long userId,
            @RequestBody ExhibitReportRequest request
    );
}
