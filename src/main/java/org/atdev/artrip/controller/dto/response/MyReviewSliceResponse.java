package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitReviewResult;
import org.atdev.artrip.service.dto.result.MyReviewResult;

import java.util.List;

public record MyReviewSliceResponse(
        List<MyReviewResponse> reviews,
        Long nextCursor,
        boolean hasNext,
        long reviewTotalCount
) {
    public static MyReviewSliceResponse from(MyReviewResult result) {
        return new MyReviewSliceResponse(
                result.reviews().stream()
                        .map(MyReviewResponse::from)
                        .toList(),

                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }

    public static MyReviewSliceResponse from(ExhibitReviewResult result) {
        return new MyReviewSliceResponse(
                result.reviews().stream().map(MyReviewResponse::from).toList(),
                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }
}
