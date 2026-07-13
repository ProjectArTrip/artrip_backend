package org.atdev.artrip.service.dto.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record AdminExhibitCreateCommand(
        Long adminId,
        String title,
        String description,
        String posterUrl,
        String ticketUrl,
        LocalDate startDate,
        LocalDate endDate,
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
        Set<String> styles,
        Long exhibitReportId
) {
    public static AdminExhibitCreateCommand of(Long adminId, String title, String description, String posterUrl, String ticketUrl,
                                               LocalDate startDate, LocalDate endDate, String exhibitHallName, String country, String region,
                                               String address, String openingHours, String phone, BigDecimal latitude, BigDecimal longitude,
                                               Set<String> genres, Set<String> styles) {
        return new AdminExhibitCreateCommand(
                adminId,
                title,
                description,
                posterUrl,
                ticketUrl,
                startDate,
                endDate,
                exhibitHallName,
                country,
                region,
                address,
                openingHours,
                phone,
                latitude,
                longitude,
                resolveIsDomestic(country),
                genres,
                styles,
                null
        );
    }

    public static AdminExhibitCreateCommand of(Long adminId, String title, String description, String posterUrl, String ticketUrl,
                                               LocalDate startDate, LocalDate endDate, String exhibitHallName, String country, String region,
                                               String address, String openingHours, String phone, BigDecimal latitude, BigDecimal longitude,
                                               Set<String> genres, Set<String> styles, Long exhibitReportId) {
        return new AdminExhibitCreateCommand(
                adminId,
                title,
                description,
                posterUrl,
                ticketUrl,
                startDate,
                endDate,
                exhibitHallName,
                country,
                region,
                address,
                openingHours,
                phone,
                latitude,
                longitude,
                resolveIsDomestic(country),
                genres,
                styles,
                exhibitReportId
        );
    }

    private static boolean resolveIsDomestic(String country) {
        return "한국".equals(country);
    }
}
