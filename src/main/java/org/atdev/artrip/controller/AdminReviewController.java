package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminSearchReviewRequest;
import org.atdev.artrip.controller.dto.response.AdminReviewListResponse;
import org.atdev.artrip.controller.spec.AdminReviewSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminReviewService;
import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController implements AdminReviewSpecification {

    private final AdminReviewService adminReviewService;

    @Override
    @GetMapping
    public ResponseEntity<AdminReviewListResponse> list(
            @LoginUser Long adminId,
            AdminSearchReviewRequest request,
            @PageableDefault Pageable pageable
    ) {
        AdminReviewSearchCommand command = request.toCommand(adminId);
        AdminReviewListResult result = adminReviewService.list(adminId, command, pageable);
        return ResponseEntity.ok(AdminReviewListResponse.from(result));

    }

}
