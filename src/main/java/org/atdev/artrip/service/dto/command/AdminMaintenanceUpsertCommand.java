package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.MaintenanceState;

import java.time.LocalDateTime;

public record AdminMaintenanceUpsertCommand(
        Long userId,
        MaintenanceState state,
        String title,
        String message,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String buttonText,
        Boolean forceExit,
        Integer refreshAfterSeconds
) {
}
