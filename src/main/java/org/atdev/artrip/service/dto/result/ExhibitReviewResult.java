package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.review.Review;
import org.springframework.data.domain.Slice;

import java.util.List;

public record ExhibitReviewResult(
        List<Review> reviews,
        Long nextCursor,
        boolean hasNext,
        long totalCount
) {
    public static ExhibitReviewResult of(Slice<Review> slice, long totalCount){

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getContent().size() - 1).getReviewId()
                : null;

        return new ExhibitReviewResult(slice.getContent(), nextCursor, slice.hasNext(),totalCount);
    }


}
