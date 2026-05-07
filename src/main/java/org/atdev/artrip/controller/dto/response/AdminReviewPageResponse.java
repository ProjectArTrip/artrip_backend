package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminReviewResult;
import org.atdev.artrip.utils.page.PageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminReviewPageResponse(
        List<AdminReviewResponse> reviews,
        PageDTO page
) {

    public static AdminReviewPageResponse from(Page<AdminReviewResult> resultPage) {
        return new AdminReviewPageResponse(
                resultPage.getContent().stream()
                        .map(AdminReviewResponse::from)
                        .toList(),
                PageDTO.from(resultPage)
        );
    }

}
