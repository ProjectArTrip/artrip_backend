package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.review.Review;
import org.springframework.data.domain.Slice;

import java.util.List;

public record MyReviewResult(
        List<Review> contents,
        Long nextCursor,
        boolean hasNext
) {
    public static MyReviewResult from(Slice<Review> slice) {

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getContent().size() - 1).getReviewId()
                : null;

        return new MyReviewResult(slice.getContent(), nextCursor, slice.hasNext());
    }
}
