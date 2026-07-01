package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.CurationSummaryResult;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;

import java.util.List;

public record CurationSummaryResponse(
        Long curationId,
        String title,
        List<ExhibitFilterResult.ExhibitItem> exhibits
) {

    public static CurationSummaryResponse from(CurationSummaryResult result) {
        return new CurationSummaryResponse(
                result.curationId(),
                result.title(),
                result.exhibits()
        );
    }
}
