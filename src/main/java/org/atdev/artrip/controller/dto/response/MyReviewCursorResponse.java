package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitReviewResult;
import org.atdev.artrip.service.dto.result.MyReviewResult;

import java.util.List;

public record MyReviewCursorResponse(
        List<MyReviewResponse> reviews,
        Long nextCursor,
        boolean hasNext,
        long reviewTotalCount
) {
    public static MyReviewCursorResponse from(MyReviewResult result) {
        return new MyReviewCursorResponse(
                result.reviews().stream()
                        .map(MyReviewResponse::from)
                        .toList(),

                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }

    public static MyReviewCursorResponse from(ExhibitReviewResult result) {
        return new MyReviewCursorResponse(
                result.reviews().stream().map(MyReviewResponse::from).toList(),
                result.nextCursor(),
                result.hasNext(),
                result.totalCount()
        );
    }
}
