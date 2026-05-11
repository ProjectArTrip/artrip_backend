package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.domain.maintenance.Maintenance;

import java.time.LocalDateTime;

public record MaintenanceStatusResult(
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
    public static MaintenanceStatusResult empty() {
        return new MaintenanceStatusResult(
                false,
                false,
                MaintenanceState.NORMAL,
                null,
                null,
                null,
                null,
                null,
                false,
                0,
                0L
        );
    }

    public static MaintenanceStatusResult from(Maintenance maintenance, LocalDateTime now) {
        return new MaintenanceStatusResult(
                true,
                maintenance.isActiveAt(now),
                maintenance.getState(),
                maintenance.getTitle(),
                maintenance.getMessage(),
                maintenance.getStartAt(),
                maintenance.getEndAt(),
                maintenance.getButtonText(),
                maintenance.isForceExit(),
                maintenance.getRefreshAfterSeconds(),
                maintenance.getVersion()
        );
    }
}
