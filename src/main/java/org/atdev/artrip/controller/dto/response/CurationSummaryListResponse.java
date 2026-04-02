package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.CurationSummaryListResult;
import org.atdev.artrip.service.dto.result.CurationSummaryResult;

import java.util.List;

public record CurationSummaryListResponse(
        List<CurationSummaryResult> curations
) {
    public static CurationSummaryListResponse from(CurationSummaryListResult result) {
        return new CurationSummaryListResponse(result.curations());
    }
}
