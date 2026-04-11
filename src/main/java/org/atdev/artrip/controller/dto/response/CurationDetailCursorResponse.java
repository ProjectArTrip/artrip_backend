package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;

import java.util.List;

public record CurationDetailCursorResponse(
        String title,
        List<ExhibitFilterResult.ExhibitItem> exhibits,
        boolean hasNext,
        Long nextCursor
) {

    public static CurationDetailCursorResponse from(CurationDetailCursorResult result) {
        return new CurationDetailCursorResponse(
                result.title(),
                result.exhibits(),
                result.hasNext(),
                result.nextCursor()
        );
    }
}
