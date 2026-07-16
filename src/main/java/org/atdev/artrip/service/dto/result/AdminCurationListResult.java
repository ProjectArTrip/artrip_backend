package org.atdev.artrip.service.dto.result;

import java.time.LocalDate;

public record AdminCurationListResult(
        Long curationId,
        String title,
        int exhibitCount,
        LocalDate createdAt,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active
) {
}
