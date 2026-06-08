package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminReviewRejectRequest;
import org.atdev.artrip.controller.dto.request.AdminSearchReviewRequest;
import org.atdev.artrip.controller.dto.response.AdminReviewResponse;
import org.atdev.artrip.controller.spec.AdminReviewSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminReviewService;
import org.atdev.artrip.service.dto.command.AdminReviewRejectCommand;
import org.atdev.artrip.service.dto.result.AdminReviewResult;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController implements AdminReviewSpecification {

    private final AdminReviewService adminReviewService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<AdminReviewResponse>> list(
            @LoginUser Long adminId,
            AdminSearchReviewRequest request,
            PageQuery pageQuery
    ) {
        Page<AdminReviewResult> page = adminReviewService.list(adminId, request.toCommand(), pageQuery.toPageable());
        return ResponseEntity.ok(PageResponse.from(page, AdminReviewResponse::from));
    }

    @Override
    @GetMapping("/{reviewId}")
    public ResponseEntity<AdminReviewResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    ) {
        AdminReviewResponse response = AdminReviewResponse.from(
                adminReviewService.detail(adminId, reviewId));

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{reviewId}/approve")
    public ResponseEntity<Void> approve(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    ) {
        adminReviewService.approveReview(adminId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{reviewId}/reject")
    public ResponseEntity<Void> reject(
            @LoginUser Long adminId,
            @PathVariable Long reviewId,
            @Valid @RequestBody AdminReviewRejectRequest request
    ) {
        AdminReviewRejectCommand command = request.toCommand(adminId, reviewId);
        adminReviewService.rejectReview(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long reviewId
    ) {
        adminReviewService.delete(adminId, reviewId);
        return ResponseEntity.noContent().build();
    }


}
