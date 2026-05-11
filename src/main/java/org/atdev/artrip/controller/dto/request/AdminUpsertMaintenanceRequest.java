package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.global.apipayload.code.error.MaintenanceErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.command.AdminMaintenanceUpsertCommand;

import java.time.LocalDateTime;
import java.util.Arrays;

public record AdminUpsertMaintenanceRequest(

        @NotBlank(message = "점검 상태")
        String state,
        String title,
        String message,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String buttonText,
        Boolean forceExit,
        Integer refreshAfterSeconds
) {

    public static AdminMaintenanceUpsertCommand toCommand(Long userId, AdminUpsertMaintenanceRequest request) {

        MaintenanceState maintenanceState = Arrays.stream(MaintenanceState.values())
                .filter(value -> value.name().equalsIgnoreCase(request.state()) || value.getLabel().equals(request.state()))
                .findFirst()
                .orElseThrow(() -> new GeneralException(MaintenanceErrorCode._MAINTENANCE_INVALID_REQUEST));

        return new AdminMaintenanceUpsertCommand(
                userId,
                maintenanceState,
                request.title(),
                request.message(),
                request.startAt(),
                request.endAt(),
                request.buttonText(),
                request.forceExit(),
                request.refreshAfterSeconds()
        );
    }
}
