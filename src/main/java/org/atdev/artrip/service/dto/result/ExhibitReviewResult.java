package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.controller.dto.response.ReviewExhibitResponse;

import java.util.List;

public record ExhibitReviewResult(
        List<ReviewExhibitResponse> reviews,
        Long nextCursor,
        boolean hasNext,
        long totalCount
) {
    public static ExhibitReviewResult of(){

    }


}
