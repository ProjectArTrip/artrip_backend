package org.atdev.artrip.service.dto.command;

import java.time.LocalDate;

public record AdminCurationSearchCommand(
        String keyword,
        Boolean active,
        LocalDate startDate,
        LocalDate endDate
) {

    public static AdminCurationSearchCommand of(String keyword, Boolean active, LocalDate startDate, LocalDate endDate) {
        return new AdminCurationSearchCommand(
                (keyword == null || keyword.isBlank()) ? null : keyword.trim(),
                active,
                startDate,
                endDate
        );
    }
}
