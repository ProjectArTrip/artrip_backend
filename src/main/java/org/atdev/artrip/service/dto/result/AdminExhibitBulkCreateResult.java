package org.atdev.artrip.service.dto.result;

import java.util.List;

public record AdminExhibitBulkCreateResult(
        int createdCount,
        int skippedCount,
        List<Long> exhibitIds
) {
    public static AdminExhibitBulkCreateResult of(List<Long> exhibitIds, int skippedCount) {
        return new AdminExhibitBulkCreateResult(exhibitIds.size(), skippedCount, exhibitIds);
    }
}
