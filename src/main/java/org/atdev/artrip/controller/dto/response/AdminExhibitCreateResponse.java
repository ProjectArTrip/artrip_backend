package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminExhibitCreateResult;

public record AdminExhibitCreateResponse(
        Long exhibitId
) {
    public static AdminExhibitCreateResponse from(AdminExhibitCreateResult result) {
        return new AdminExhibitCreateResponse(result.exhibitId());
    }
}
