package org.atdev.artrip.service.dto.result;

import java.util.List;

public record CurationDetailCursorResult(
        String title,
        List<ExhibitFilterResult.ExhibitItem> exhibits,
        boolean hasNext,
        Long nextCursor
) {

    public static CurationDetailCursorResult of(String title, List<ExhibitFilterResult.ExhibitItem> exhibits, boolean hasNext, Long nextCursor){
        return new CurationDetailCursorResult(title, exhibits, hasNext, nextCursor);
    }
}
