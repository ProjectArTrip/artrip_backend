package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.atdev.artrip.service.dto.command.AdminExhibitCreateCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record AdminCreateExhibitRequest(
        @NotBlank String title,
        String description,

        @Pattern(regexp = "^https?://.*")
        String posterUrl,

        @Pattern(regexp = "^https?://.*")
        String ticketUrl,

        LocalDate startDate,
        LocalDate endDate,
        @NotBlank String exhibitHallName,
        @NotBlank String country,
        String region,
        String address,
        String openingHours,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        Set<String> genres,
        Set<String> styles
) {
    public AdminExhibitCreateCommand toCommand(Long adminId) {
        return AdminExhibitCreateCommand.of(
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
                genres == null ? Set.of() : genres,
                styles == null ? Set.of() : styles
        );
    }
}
