package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record AdminExhibitUpdateCommand(
        Long adminId,
        Long exhibitId,
        String title,
        String description,
        String posterUrl,
        String ticketUrl,
        LocalDate startDate,
        LocalDate endDate,
        Status status,
        String exhibitHallName,
        String country,
        String region,
        String address,
        String openingHours,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isDomestic,
        Set<String> genres,
        Set<String> styles
) {

    public static AdminExhibitUpdateCommand of(Long adminId, Long exhibitId,
                                               String title, String description,
                                               String posterUrl, String ticketUrl,
                                               LocalDate startDate, LocalDate endDate, Status status,
                                               String exhibitHallName, String country, String region,
                                               String address, String openingHours, String phone,
                                               BigDecimal latitude, BigDecimal longitude,
                                               Set<String> genres, Set<String> styles) {
        return new AdminExhibitUpdateCommand(
                adminId, exhibitId,
                title, description, posterUrl, ticketUrl,
                startDate, endDate, status,
                exhibitHallName, country, region,
                address, openingHours, phone,
                latitude, longitude,
                resolveIsDomestic(country),
                genres, styles);
    }

    private static boolean resolveIsDomestic(String country) {
        return "한국".equals(country);
    }
}
