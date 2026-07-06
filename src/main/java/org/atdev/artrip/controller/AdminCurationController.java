package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminCreateCurationRequest;
import org.atdev.artrip.controller.dto.request.AdminCurationSearchRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateCurationRequest;
import org.atdev.artrip.controller.dto.response.AdminCurationCreateResponse;
import org.atdev.artrip.controller.dto.response.AdminCurationDetailResponse;
import org.atdev.artrip.controller.dto.response.AdminCurationListResponse;
import org.atdev.artrip.controller.spec.AdminCurationSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminCurationService;
import org.atdev.artrip.service.dto.command.AdminCurationCreateCommand;
import org.atdev.artrip.service.dto.result.AdminCurationCreateResult;
import org.atdev.artrip.service.dto.result.AdminCurationDetailResult;
import org.atdev.artrip.service.dto.result.AdminCurationListResult;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/curations")
@RequiredArgsConstructor
public class AdminCurationController implements AdminCurationSpecification {

    private final AdminCurationService adminCurationService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<AdminCurationListResponse>> list(
            @LoginUser Long adminId,
            AdminCurationSearchRequest request,
            PageQuery pageQuery
    ) {
        Page<AdminCurationListResult> page = adminCurationService.list(adminId, request.toCommand(), pageQuery.toPageable());
        return ResponseEntity.ok(PageResponse.from(page, AdminCurationListResponse::from));
    }

    @Override
    @GetMapping("/{curationId}")
    public ResponseEntity<AdminCurationDetailResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long curationId
    ) {
        AdminCurationDetailResult result = adminCurationService.detail(adminId, curationId);
        return ResponseEntity.ok(AdminCurationDetailResponse.from(result));
    }

    @Override
    @PostMapping
    public ResponseEntity<AdminCurationCreateResponse> create(
            @LoginUser Long adminId,
            @RequestBody AdminCreateCurationRequest request
    ) {
        AdminCurationCreateCommand command = request.toCommand(adminId);
        AdminCurationCreateResult result = adminCurationService.create(command);
        return ResponseEntity.ok(AdminCurationCreateResponse.from(result));
    }

    @Override
    @PutMapping("/{curationId}")
    public ResponseEntity<Void> update(
            @LoginUser Long adminId,
            @PathVariable Long curationId,
            @RequestBody AdminUpdateCurationRequest request
    ) {
        adminCurationService.update(request.toCommand(adminId, curationId));
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{curationId}")
    public ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long curationId
    ) {
        adminCurationService.delete(adminId, curationId);
        return ResponseEntity.noContent().build();
    }
}
