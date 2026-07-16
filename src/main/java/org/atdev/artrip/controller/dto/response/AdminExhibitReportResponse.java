package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.ExhibitReportStatus;
import org.atdev.artrip.service.dto.result.AdminExhibitReportResult;

import java.time.LocalDateTime;

public record AdminExhibitReportResponse(
        Long exhibitReportId,
        String title,
        String country,
        ExhibitReportStatus status,
        String reporterName,
        Long exhibitId,
        LocalDateTime createdAt
) {
    public static AdminExhibitReportResponse from(AdminExhibitReportResult result) {
        return new AdminExhibitReportResponse(
                result.exhibitReportId(),
                result.title(),
                result.country(),
                result.status(),
                result.reporterName(),
                result.exhibitId(),
                result.createdAt()
        );
    }
}
