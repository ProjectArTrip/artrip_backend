package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.command.AdminExhibitUpdateCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record AdminUpdateExhibitRequest(
        @NotBlank String title,
        String description,

        @Pattern(regexp = "^https?://.*")
        String posterUrl,

        @Pattern(regexp = "^https?://.*")
        String ticketUrl,

        LocalDate startDate,
        LocalDate endDate,
        @NotNull Status status,
        @NotBlank String exhibitHallName,
        @NotBlank String country,
        String region,
        String address,
        String openingHours,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isDomestic,
        Set<String> genres,
        Set<String> styles
) {
    public AdminExhibitUpdateCommand toCommand(Long adminId, Long exhibitId) {
        return AdminExhibitUpdateCommand.of(
                adminId,
                exhibitId,
                title,
                description,
                posterUrl,
                ticketUrl,
                startDate,
                endDate,
                status,
                exhibitHallName,
                country,
                region,
                address,
                openingHours,
                phone,
                latitude,
                longitude,
                genres == null ? Set.of() : genres,
                styles == null ? Set.of() : styles
        );
    }
}
