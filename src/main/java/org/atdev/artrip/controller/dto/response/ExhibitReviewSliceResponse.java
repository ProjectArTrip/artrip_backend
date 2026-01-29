package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitReviewResult;

import java.util.List;

public record ExhibitReviewSliceResponse(
        List<ReviewListResponse> reviews,
        Long nextCursor,
        boolean hasNext,
        long reviewTotalCount
) {

    public static ExhibitReviewSliceResponse from(ExhibitReviewResult result){

        return new ExhibitReviewSliceResponse(
                result.reviews().stream()
                        .map(ReviewListResponse::from)
                        .toList(),

                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }

}
