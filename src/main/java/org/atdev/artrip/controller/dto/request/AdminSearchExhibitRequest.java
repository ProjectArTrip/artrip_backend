package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.service.dto.command.AdminExhibitSearchCommand;

import java.time.LocalDate;

public record AdminSearchExhibitRequest(
        String status,
        String country,
        String region,
        String genre,
        LocalDate startDate,
        LocalDate endDate,
        String keyword
) {

    public AdminExhibitSearchCommand toCommand() {
        return AdminExhibitSearchCommand.of(
                status,
                country,
                region,
                genre,
                startDate,
                endDate,
                keyword
        );
    }
}
