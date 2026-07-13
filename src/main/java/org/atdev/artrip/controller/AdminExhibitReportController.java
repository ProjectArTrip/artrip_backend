package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.AdminExhibitReportResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminExhibitReportService;
import org.atdev.artrip.service.dto.result.AdminExhibitReportResult;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/exhibit-reports")
public class AdminExhibitReportController {

    private final AdminExhibitReportService adminExhibitReportService;

    @GetMapping
    public ResponseEntity<PageResponse<AdminExhibitReportResponse>> list(
            @LoginUser Long adminId,
            PageQuery pageQuery
    ) {
        Page<AdminExhibitReportResult> page = adminExhibitReportService.list(adminId, pageQuery.toPageable());
        return ResponseEntity.ok(PageResponse.from(page, AdminExhibitReportResponse::from));
    }
}
