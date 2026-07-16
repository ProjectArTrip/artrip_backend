package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminCurationCreateResult;

public record AdminCurationCreateResponse(
        Long curationId
) {
    public static AdminCurationCreateResponse from(AdminCurationCreateResult result) {
        return new AdminCurationCreateResponse(result.curationId());
    }
}
