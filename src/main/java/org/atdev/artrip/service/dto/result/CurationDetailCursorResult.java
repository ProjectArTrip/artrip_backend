package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.curation.CurationExhibit;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

public record CurationDetailCursorResult(
        String title,
        List<ExhibitFilterResult.ExhibitItem> exhibits,
        boolean hasNext,
        Long nextCursor
) {

    public static CurationDetailCursorResult of(String title, Slice<CurationExhibit> slice, Set<Long> favoriteExhibitIds){
        if (slice == null || slice.isEmpty()) {
            return new CurationDetailCursorResult(title, List.of(), false, null);
        }

        List<ExhibitFilterResult.ExhibitItem> items = slice.getContent().stream()
                .map(curationExhibit -> ExhibitFilterResult.ExhibitItem.from(curationExhibit, favoriteExhibitIds)).toList();

        Long nextCursor = null;
        if(slice.hasNext()) {
            CurationExhibit last = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = (long) last.getDisplayOrder();
        }

        return new CurationDetailCursorResult(title, items, slice.hasNext(), nextCursor);
    }
}
