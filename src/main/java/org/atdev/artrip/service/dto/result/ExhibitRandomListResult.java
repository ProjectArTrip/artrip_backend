package org.atdev.artrip.service.dto.result;

import java.util.List;

public record ExhibitRandomListResult(
        List<ExhibitRandomResult> results
) {
    public static ExhibitRandomListResult from(List<ExhibitRandomResult> results) {
        return new ExhibitRandomListResult(results);
    }
}
