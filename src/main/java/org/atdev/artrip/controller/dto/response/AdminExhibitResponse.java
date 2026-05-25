package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.result.AdminExhibitResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AdminExhibitResponse(
        Long exhibitId,
        String title,
        String description,
        String posterUrl,
        String ticketUrl,
        LocalDate startDate,
        LocalDate endDate,
        Status status,
        Long exhibitHallId,
        String exhibitHallName,
        String country,
        String region,
        String address,
        String openingHours,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isDomestic,
        List<String> genres,
        List<String> styles
) {
    public static AdminExhibitResponse from(AdminExhibitResult result) {
        return new AdminExhibitResponse(
                result.exhibitId(),
                result.title(),
                result.description(),
                result.posterUrl(),
                result.ticketUrl(),
                result.startDate(),
                result.endDate(),
                result.status(),
                result.exhibitHallId(),
                result.exhibitHallName(),
                result.country(),
                result.region(),
                result.address(),
                result.openingHours(),
                result.phone(),
                result.latitude(),
                result.longitude(),
                result.isDomestic(),
                result.genres(),
                result.styles()
        );
    }
}
