package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ExhibitReportRequest;
import org.atdev.artrip.controller.spec.ExhibitReportSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.ExhibitReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibit-report")
public class ExhibitReportController implements ExhibitReportSpecification {

    private final ExhibitReportService exhibitReportService;

    @Override
    @PostMapping
    public ResponseEntity<Long> report(
            @LoginUser Long userId,
            @Valid @RequestBody ExhibitReportRequest request
    ) {
        Long reportId = exhibitReportService.create(userId, request.toCommand());
        return ResponseEntity.ok(reportId);
    }
}
