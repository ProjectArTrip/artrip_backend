package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminExhibitBulkCreateResult;

import java.util.List;

public record AdminExhibitBulkCreateResponse(
        int createdCount,
        List<Long> exhibitIds
) {

    public static AdminExhibitBulkCreateResponse from(AdminExhibitBulkCreateResult result) {
        return new AdminExhibitBulkCreateResponse(result.createdCount(), result.exhibitIds());
    }
}
