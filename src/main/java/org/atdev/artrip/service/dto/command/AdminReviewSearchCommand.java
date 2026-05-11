package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.global.apipayload.code.error.CommonErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import java.time.LocalDate;

public record AdminReviewSearchCommand(
        ReviewStatus status,
        String keyword,
        Long userId,
        Boolean stampIssued,
        LocalDate startDate,
        LocalDate endDate
) {
    public static AdminReviewSearchCommand of (String status, String keyword, Long userId, Boolean stampIssued, LocalDate startDate, LocalDate endDate) {
        return new AdminReviewSearchCommand(
                parseStatus(status),
                keyword,
                userId,
                stampIssued,
                startDate,
                endDate
        );
    }

    private static ReviewStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return ReviewStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(CommonErrorCode._BAD_REQUEST);
        }
    }
}
