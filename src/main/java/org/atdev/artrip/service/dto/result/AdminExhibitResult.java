package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.keyword.Keyword;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record AdminExhibitResult(
        Long exhibitId,
        String title,
        String description,
        String posterUrl,
        String ticketUrl,
        LocalDate startDate,
        LocalDate endDate,
        Status status,
        Long exhibitHallId,
        String exhibitHallName,
        String country,
        String region,
        String address,
        String openingHours,
        String phone,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isDomestic,
        List<String> genres,
        List<String> styles
) {
    public static AdminExhibitResult from(Exhibit exhibit) {
        ExhibitHall hall = exhibit.getExhibitHall();

        Map<KeywordType, List<String>> grouped = exhibit.getKeywords().stream()
                .collect(Collectors.groupingBy(
                        Keyword::getType,
                        Collectors.mapping(Keyword::getName, Collectors.toList())
                ));
        List<String> genres = grouped.getOrDefault(KeywordType.GENRE, List.of());
        List<String> styles = grouped.getOrDefault(KeywordType.STYLE, List.of());

        return new AdminExhibitResult(
                exhibit.getExhibitId(),
                exhibit.getTitle(),
                exhibit.getDescription(),
                exhibit.getPosterUrl(),
                exhibit.getTicketUrl(),
                exhibit.getStartDate(),
                exhibit.getEndDate(),
                exhibit.getStatus(),
                hall.getExhibitHallId(),
                hall.getName(),
                hall.getCountry(),
                hall.getRegion(),
                hall.getAddress(),
                hall.getOpeningHours(),
                hall.getPhone(),
                hall.getLatitude(),
                hall.getLongitude(),
                hall.getIsDomestic(),
                genres,
                styles
        );
    }
}
