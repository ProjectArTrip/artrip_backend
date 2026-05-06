package org.atdev.artrip.service.dto.command;

import java.time.LocalDate;

public record AdminReviewSearchCommand(
        String status,
        String keyword,
        Long adminId,
        LocalDate startDate,
        LocalDate endDate

) {
}
