package org.atdev.artrip.controller.dto.response;

import lombok.*;
import org.atdev.artrip.service.dto.result.MyReviewResult;

import java.util.List;

public record ReviewSliceResponse(
        List<ReviewListResponse> summaries,
        Long nextCursor,
        boolean hasNext
) {
    public static ReviewSliceResponse from(MyReviewResult result) {
        return new ReviewSliceResponse(
                result.contents().stream()
                        .map(ReviewListResponse::from)
                        .toList(),

                result.nextCursor(),
                result.hasNext()
        );
    }
}
