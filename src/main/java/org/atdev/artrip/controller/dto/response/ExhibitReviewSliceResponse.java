package org.atdev.artrip.controller.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExhibitReviewSliceResponse {
    private List<ReviewExhibitResponse> reviews;
    private Long nextCursor;
    private boolean hasNext;
    private long reviewTotalCount;
}
