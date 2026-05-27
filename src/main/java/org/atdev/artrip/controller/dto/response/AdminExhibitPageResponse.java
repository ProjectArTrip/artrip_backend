package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminExhibitListResult;
import org.atdev.artrip.utils.page.PageDTO;

import java.util.List;

public record AdminExhibitPageResponse(
        List<AdminExhibitListItemResponse> exhibits,
        PageDTO page
) {

    public static AdminExhibitPageResponse from(AdminExhibitListResult result) {
        return new AdminExhibitPageResponse(
                result.exhibits().stream()
                        .map(AdminExhibitListItemResponse::from)
                        .toList(),
                result.pageInfo()
        );
    }
}
