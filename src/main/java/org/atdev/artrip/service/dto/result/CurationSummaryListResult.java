package org.atdev.artrip.service.dto.result;

import java.util.List;

public record CurationSummaryListResult(
        List<CurationSummaryResult> curations

) {
    public static CurationSummaryListResult from(List<CurationSummaryResult> exhibits) {
        return new CurationSummaryListResult(exhibits);
    }
}
