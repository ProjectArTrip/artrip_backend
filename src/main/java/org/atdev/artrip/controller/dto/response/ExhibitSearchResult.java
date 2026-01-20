package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.constants.Status;

import java.time.LocalDate;

@Builder
public record ExhibitSearchResult(
        Long exhibitId,
        String title,
        String posterUrl,
        Status status,
        String hallName,
        String region,
        String country,
        LocalDate startDate,
        LocalDate endDate,
        boolean isFavorite

        ) {
}
