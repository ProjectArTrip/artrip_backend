package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminCreateExhibitRequest;
import org.atdev.artrip.controller.dto.request.AdminSearchExhibitRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateExhibitRequest;
import org.atdev.artrip.controller.dto.response.AdminExhibitBulkCreateResponse;
import org.atdev.artrip.controller.dto.response.AdminExhibitCreateResponse;
import org.atdev.artrip.controller.dto.response.AdminExhibitPageResponse;
import org.atdev.artrip.controller.dto.response.AdminExhibitResponse;
import org.atdev.artrip.controller.spec.AdminExhibitSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminExhibitService;
import org.atdev.artrip.service.dto.command.AdminExhibitCreateCommand;
import org.atdev.artrip.service.dto.command.AdminExhibitSearchCommand;
import org.atdev.artrip.service.dto.command.AdminExhibitUpdateCommand;
import org.atdev.artrip.service.dto.result.AdminExhibitBulkCreateResult;
import org.atdev.artrip.service.dto.result.AdminExhibitCreateResult;
import org.atdev.artrip.service.dto.result.AdminExhibitListResult;
import org.atdev.artrip.service.dto.result.AdminExhibitResult;
import org.atdev.artrip.utils.page.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/exhibits")
@RequiredArgsConstructor
public class AdminExhibitController implements AdminExhibitSpecification {

    private final AdminExhibitService adminExhibitService;

    @Override
    @GetMapping
    public ResponseEntity<AdminExhibitPageResponse> list(
            @LoginUser Long adminId,
            AdminSearchExhibitRequest request,
            Criteria criteria
    ) {
        AdminExhibitSearchCommand command = request.toCommand();
        AdminExhibitListResult result = adminExhibitService.list(adminId, command, criteria.toPageable());
        return ResponseEntity.ok(AdminExhibitPageResponse.from(result));
    }

    @Override
    @GetMapping("/{exhibitId}")
    public ResponseEntity<AdminExhibitResponse> detail(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId
    ) {
        AdminExhibitResult result = adminExhibitService.detail(adminId, exhibitId);
        return ResponseEntity.ok(AdminExhibitResponse.from(result));
    }

    @Override
    @PostMapping
    public ResponseEntity<AdminExhibitCreateResponse> create(
            @LoginUser Long adminId,
            @Valid @RequestBody AdminCreateExhibitRequest request
            ) {
        AdminExhibitCreateCommand command = request.toCommand(adminId);
        AdminExhibitCreateResult result = adminExhibitService.create(command);

        return ResponseEntity.ok(AdminExhibitCreateResponse.from(result));
    }

    @Override
    @PostMapping("/file")
    public ResponseEntity<AdminExhibitBulkCreateResponse> bulkCreateFromCsv(
            @LoginUser Long adminId,
            @RequestParam("file") MultipartFile file
    ) {

        AdminExhibitBulkCreateResult result = adminExhibitService.bulkCreateFromCsv(adminId, file);
        return ResponseEntity.ok(AdminExhibitBulkCreateResponse.from(result));
    }

    @Override
    @PutMapping("/{exhibitId}")
    public ResponseEntity<Void> update(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId,
            @Valid @RequestBody AdminUpdateExhibitRequest request
            ) {
        AdminExhibitUpdateCommand command = request.toCommand(adminId, exhibitId);
        adminExhibitService.update(command);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{exhibitId}")
    public ResponseEntity<Void> delete(
            @LoginUser Long adminId,
            @PathVariable Long exhibitId
    ) {
        adminExhibitService.delete(adminId, exhibitId);
        return ResponseEntity.noContent().build();
    }


}
