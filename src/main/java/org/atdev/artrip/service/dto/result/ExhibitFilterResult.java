package org.atdev.artrip.service.dto.result;


import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.utils.DateTimeUtils;
import org.atdev.artrip.utils.ImageUrlUtils;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record ExhibitFilterResult(
        List<ExhibitItem> items,
        boolean hasNext,
        Long nextCursor,
        LocalDate nextCursorDate,
        long totalCount
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
    ) {
        public static ExhibitItem from(CurationExhibit curationExhibit, Set<Long> favoriteExhibitIds) {
            Exhibit exhibit = curationExhibit.getExhibit();
            ExhibitHall hall = exhibit.getExhibitHall();
            return new ExhibitItem(
                    exhibit.getExhibitId(),
                    exhibit.getTitle(),
                    ImageUrlUtils.posterUrlDefault(exhibit.getPosterUrl()),
                    exhibit.getStatus(),
                    DateTimeUtils.convertDate(exhibit.getStartDate(), exhibit.getEndDate()),
                    favoriteExhibitIds.contains(exhibit.getExhibitId()),
                    hall.getName(),
                    hall.getCountry(),
                    hall.getRegion()
            );
        }
    }

    public static ExhibitFilterResult of(Slice<Exhibit> slice, Set<Long> favorites, long totalCount) {

        if (slice == null || slice.isEmpty()) {
            return new ExhibitFilterResult(List.of(), false, null, null, 0L);
        }

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

        Long nextCursorId = null;
        LocalDate nextCursorDate = null;
        if (slice.hasNext() && !items.isEmpty()) {
            Exhibit lastExhibit = slice.getContent().get(slice.getContent().size() - 1);
            nextCursorId = lastExhibit.getExhibitId();
            nextCursorDate = lastExhibit.getStartDate();
        }

        return new ExhibitFilterResult(
                items,
                slice.hasNext(),
                nextCursorId,
                nextCursorDate,
                totalCount
        );
    }
}
