package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.service.dto.command.AdminCurationSearchCommand;

import java.time.LocalDate;

public record AdminCurationSearchRequest(
        String keyword,
        Boolean active,
        LocalDate startDate,
        LocalDate endDate
) {

    public AdminCurationSearchCommand toCommand() {
        return AdminCurationSearchCommand.of(keyword, active, startDate, endDate);
    }
}
