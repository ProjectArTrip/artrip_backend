package org.atdev.artrip.service.dto.result;

import java.util.List;

public record AdminExhibitBulkCreateResult(
        int createdCount,
        List<Long> exhibitIds
) {
    public static AdminExhibitBulkCreateResult of(List<Long> exhibitIds) {
        return new AdminExhibitBulkCreateResult(exhibitIds.size(), exhibitIds);
    }
}
