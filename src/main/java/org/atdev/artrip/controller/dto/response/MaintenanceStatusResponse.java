package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.service.dto.result.MaintenanceStatusResult;

import java.time.LocalDateTime;

public record MaintenanceStatusResponse(
        boolean configured,
        boolean active,
        MaintenanceState state,
        String title,
        String message,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String buttonText,
        boolean forceExit,
        int refreshAfterSeconds,
        long version
) {
    public static MaintenanceStatusResponse from(MaintenanceStatusResult result) {
        return new MaintenanceStatusResponse(
                result.configured(),
                result.active(),
                result.state(),
                result.title(),
                result.message(),
                result.startAt(),
                result.endAt(),
                result.buttonText(),
                result.forceExit(),
                result.refreshAfterSeconds(),
                result.version()
        );
    }
}
