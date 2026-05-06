package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;

import java.time.LocalDate;

public record AdminSearchReviewRequest(
        String status, //nullable
        String keyword, //nullable
        LocalDate startDate, //nullable
        LocalDate endDate // nullable
) {
    public AdminReviewSearchCommand toCommand(Long adminId) {
        return new AdminReviewSearchCommand(
                this.status,
                this.keyword,
                adminId,
                this.startDate,
                this.endDate
        );
    }
}
