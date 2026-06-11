package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.service.dto.command.AdminExhibitCreateCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record AdminExhibitCsvPreviewResult(
        int totalCount,
        List<Row> rows
) {
    public record Row(
            String title,
            String description,
            String posterUrl,
            String ticketUrl,
            LocalDate startDate,
            LocalDate endDate,
            String exhibitHallName,
            String country,
            String region,
            String address,
            String openingHours,
            String phone,
            BigDecimal latitude,
            BigDecimal longitude,
            boolean isDomestic,
            Set<String> genres,
            Set<String> styles
    ) {
        public static Row from(AdminExhibitCreateCommand command) {
            return new Row(
                    command.title(),
                    command.description(),
                    command.posterUrl(),
                    command.ticketUrl(),
                    command.startDate(),
                    command.endDate(),
                    command.exhibitHallName(),
                    command.country(),
                    command.region(),
                    command.address(),
                    command.openingHours(),
                    command.phone(),
                    command.latitude(),
                    command.longitude(),
                    command.isDomestic(),
                    command.genres(),
                    command.styles()
            );
        }
    }

    public static AdminExhibitCsvPreviewResult from(List<AdminExhibitCreateCommand> commands) {
        List<Row> rows = commands.stream().map(Row::from).toList();
        return new AdminExhibitCsvPreviewResult(rows.size(), rows);
    }
}
