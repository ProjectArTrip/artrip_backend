package org.atdev.artrip.domain.exhibitReport;

public record ExhibitReportCreatedEvent(
        Long exhibitReportId,
        Long userId,
        String title
) {
}
