package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import java.time.LocalDate;

public record AdminExhibitSearchCommand(
        Status status,
        String country,
        String region,
        String genre,
        LocalDate startDate,
        LocalDate endDate,
        String keyword
) {
    public static AdminExhibitSearchCommand of (String status, String country, String region, String genre, LocalDate startDate, LocalDate endDate, String keyword) {
        return new AdminExhibitSearchCommand(
                parseStatus(status),
                country,
                region,
                genre,
                startDate,
                endDate,
                keyword
        );
    }

    private static Status parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_INVALID_STATUS);
        }
    }
}
