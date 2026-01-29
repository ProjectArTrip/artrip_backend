package org.atdev.artrip.controller.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSliceResponse {
    private List<ReviewListResponse> reviews;
    private Long nextCursor;
    private boolean hasNext;
}
