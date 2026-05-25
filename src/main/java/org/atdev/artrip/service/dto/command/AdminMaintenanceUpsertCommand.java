package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.global.apipayload.code.error.MaintenanceErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.util.StringUtils;

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
    public void validate() {
        boolean missingRequiredValues = !StringUtils.hasText(title)
                || !StringUtils.hasText(message)
                || startAt == null
                || endAt == null
                || !StringUtils.hasText(buttonText)
                || forceExit == null
                || refreshAfterSeconds == null;

        if (missingRequiredValues) {
            throw new GeneralException(MaintenanceErrorCode._MAINTENANCE_INVALID_REQUEST);
        }

        if (!endAt.isAfter(startAt)) {
            throw new GeneralException(MaintenanceErrorCode._MAINTENANCE_INVALID_PERIOD);
        }
    }
}
