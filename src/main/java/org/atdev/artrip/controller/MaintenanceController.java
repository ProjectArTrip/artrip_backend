package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminUpsertMaintenanceRequest;
import org.atdev.artrip.controller.dto.response.MaintenanceStatusResponse;
import org.atdev.artrip.controller.spec.AdminMaintenanceSpecification;
import org.atdev.artrip.controller.spec.MaintenanceSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.MaintenanceService;
import org.atdev.artrip.service.dto.command.AdminMaintenanceUpsertCommand;
import org.atdev.artrip.service.dto.result.MaintenanceStatusResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@RequiredArgsConstructor
public class MaintenanceController implements MaintenanceSpecification, AdminMaintenanceSpecification {

    private final MaintenanceService maintenanceService;

    @Override
    @PermitAll
    @GetMapping("/maintenance")
    public ResponseEntity<MaintenanceStatusResponse> getMaintenanceStatus() {
        MaintenanceStatusResult result = maintenanceService.getMaintenanceStatus();
        return ResponseEntity.ok(MaintenanceStatusResponse.from(result));
    }

    @Override
    @GetMapping("/admin/maintenance")
    public ResponseEntity<MaintenanceStatusResponse> getAdminMaintenanceStatus(@LoginUser Long userId) {
        MaintenanceStatusResult result = maintenanceService.getAdminMaintenanceStatus(userId);
        return ResponseEntity.ok(MaintenanceStatusResponse.from(result));
    }

    @Override
    @PutMapping("/admin/maintenance")
    public ResponseEntity<MaintenanceStatusResponse> upsertMaintenance(
            @LoginUser Long userId,
            @Valid @RequestBody AdminUpsertMaintenanceRequest request) {

        AdminMaintenanceUpsertCommand command = AdminUpsertMaintenanceRequest.toCommand(userId, request);
        MaintenanceStatusResult result = maintenanceService.upsertMaintenance(command);
        return ResponseEntity.ok(MaintenanceStatusResponse.from(result));
    }

}
