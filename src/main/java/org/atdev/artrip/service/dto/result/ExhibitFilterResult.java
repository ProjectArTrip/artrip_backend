package org.atdev.artrip.service.dto.result;


import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.utils.DateTimeUtils;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

public record ExhibitFilterResult(
        List<ExhibitItem> items,
        boolean hasNext,
        Long nextCursor
) {
    public record ExhibitItem(
            Long exhibitId,
            String title,
            String posterUrl,
            Status status,
            String exhibitPeriod,
            boolean isFavorite,

            String hallName,
            String countryName,
            String regionName
    ) {}

    public static ExhibitFilterResult of(Slice<Exhibit> slice, Set<Long> favorites) {

        List<ExhibitItem> items = slice.getContent()
                .stream()
                .map(e -> new ExhibitItem(
                        e.getExhibitId(),
                        e.getTitle(),
                        e.getPosterUrl(),
                        e.getStatus(),
                        DateTimeUtils.convertDate(e.getStartDate(), e.getEndDate()),
                        favorites.contains(e.getExhibitId()),
                        e.getExhibitHall().getName(),
                        e.getExhibitHall().getCountry(),
                        e.getExhibitHall().getRegion()
                ))
                .toList();

        Long nextCursor = slice.hasNext() && !items.isEmpty()
                ? items.get(items.size() - 1).exhibitId()
                : null;

        return new ExhibitFilterResult(
                items,
                slice.hasNext(),
                nextCursor
        );
    }
}
