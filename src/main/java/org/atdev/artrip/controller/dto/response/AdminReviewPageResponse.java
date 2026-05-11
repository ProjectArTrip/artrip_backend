package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminReviewListResult;
import org.atdev.artrip.utils.page.PageDTO;

import java.util.List;

public record AdminReviewPageResponse(
        List<AdminReviewResponse> reviews,
        PageDTO page
) {

    public static AdminReviewPageResponse from(AdminReviewListResult result) {
        return new AdminReviewPageResponse(
                result.reviews().stream()
                        .map(AdminReviewResponse::from)
                        .toList(),
                result.pageInfo()
        );
    }

}
