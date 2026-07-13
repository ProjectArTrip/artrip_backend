package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.ExhibitReportStatus;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitReport.ExhibitReport;

import java.time.LocalDateTime;

public record AdminExhibitReportResult(
        Long exhibitReportId,
        String title,
        String country,
        ExhibitReportStatus status,
        String reporterName,
        Long exhibitId,
        LocalDateTime createdAt
) {

    public static AdminExhibitReportResult from(ExhibitReport report) {
        Exhibit exhibit = report.getExhibit();

        return new AdminExhibitReportResult(
                report.getExhibitReportId(),
                report.getTitle(),
                report.getCountry(),
                report.getStatus(),
                report.getUser().getNickName(),
                exhibit == null ? null : exhibit.getExhibitId(),
                report.getCreatedAt()
        );
    }
}
