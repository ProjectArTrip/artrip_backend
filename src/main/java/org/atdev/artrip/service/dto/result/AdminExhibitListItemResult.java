package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.Status;

import java.time.LocalDate;
import java.util.List;

public record AdminExhibitListItemResult(
        Long exhibitId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String country,
        String region,
        Status status,
        String hallName,
        List<String> genres
) {
}
