package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.utils.page.PageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminReviewListResult(
        List<AdminReviewResult> reviews,
        PageDTO pageInfo
) {

    public static AdminReviewListResult from(Page<AdminReviewResult> page) {
        return new AdminReviewListResult(page.getContent(), PageDTO.from(page));
    }
}
