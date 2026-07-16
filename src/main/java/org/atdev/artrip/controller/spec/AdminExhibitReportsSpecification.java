package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.response.AdminExhibitReportResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.http.ResponseEntity;

public interface AdminExhibitReportsSpecification {

    @Operation(
            summary = "제안 받은 전시 List"
    )
    ResponseEntity<PageResponse<AdminExhibitReportResponse>> list(
            @LoginUser Long adminId,
            PageQuery pageQuery
    );
}
