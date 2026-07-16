package org.atdev.artrip.domain.exhibitReport;

public record ExhibitReportRegisteredEvent(
        Long exhibitReportId,
        Long userId,
        String title,
        Long exhibitId
) {
}
