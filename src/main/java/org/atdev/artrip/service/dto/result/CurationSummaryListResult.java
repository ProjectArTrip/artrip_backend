package org.atdev.artrip.service.dto.result;

import java.util.List;

public record CurationSummaryListResult(
        List<CurationSummaryResult> curations

) {
    public static CurationSummaryListResult from(List<CurationSummaryResult> curations) {
        return new CurationSummaryListResult(curations);
    }
}
