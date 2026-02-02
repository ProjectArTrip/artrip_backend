package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.favorite.Favorite;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record FavoriteResult(
        List<FavoriteItem> items,
        boolean hasNext,
        Long nextCursor
) {
    public record FavoriteItem(
            Long favoriteId,
            Long exhibitId,
            String title,
            String posterUrl,
            Status status,
            boolean active,
            String exhibitPeriod,
            String exhibitHallName,
            String country,
            String region,
            LocalDate createdAt
    ) {}
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static FavoriteResult of(Slice<Favorite> slice) {

        List<FavoriteItem> items = slice.getContent()
                .stream()
                .map(e ->{
                    String period = e.getExhibit().getStartDate().format(FORMATTER) + " - " + e.getExhibit().getEndDate().format(FORMATTER);

                    return new FavoriteItem(
                            e.getFavoriteId(),
                            e.getExhibit().getExhibitId(),
                            e.getExhibit().getTitle(),
                            e.getExhibit().getPosterUrl(),
                            e.getExhibit().getStatus(),
                            e.isStatus(),
                            period,
                            e.getExhibit().getExhibitHall().getName(),
                            e.getExhibit().getExhibitHall().getCountry(),
                            e.getExhibit().getExhibitHall().getRegion(),
                            e.getCreatedAt()
                    );
                        }).toList();

        Long nextCursor = slice.hasNext() && !items.isEmpty()
                ? items.get(items.size() - 1).favoriteId()
                : null;

        return new FavoriteResult(
                items,
                slice.hasNext(),
                nextCursor
        );
    }
}
