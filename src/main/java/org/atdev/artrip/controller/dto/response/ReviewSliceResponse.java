package org.atdev.artrip.controller.dto.response;

import lombok.*;
import org.atdev.artrip.service.dto.result.ExhibitReviewResult;
import org.atdev.artrip.service.dto.result.MyReviewResult;

import java.util.List;

public record ReviewSliceResponse(
        List<ReviewListResponse> reviews,
        Long nextCursor,
        boolean hasNext,
        long reviewTotalCount
) {
    public static ReviewSliceResponse from(MyReviewResult result) {
        return new ReviewSliceResponse(
                result.reviews().stream()
                        .map(ReviewListResponse::from)
                        .toList(),

                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }

    public static ReviewSliceResponse from(ExhibitReviewResult result) {
        return new ReviewSliceResponse(
                result.reviews().stream().map(ReviewListResponse::from).toList(),
                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }
}
