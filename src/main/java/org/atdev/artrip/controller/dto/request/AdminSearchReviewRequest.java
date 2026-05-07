package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;

import java.time.LocalDate;

public record AdminSearchReviewRequest(
        String status,
        String keyword,
        Long userId,
        Boolean stampIssued,
        LocalDate startDate,
        LocalDate endDate
) {
    public AdminReviewSearchCommand toCommand() {
        return AdminReviewSearchCommand.of(
                status,
                keyword,
                userId,
                stampIssued,
                startDate,
                endDate
        );
    }

}
