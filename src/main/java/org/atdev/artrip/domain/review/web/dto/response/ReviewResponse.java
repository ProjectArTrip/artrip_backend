package org.atdev.artrip.domain.review.web.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long reviewId;
    private Long exhibitId;
    private LocalDate visitDate;
    private String content;
    private List<ReviewImageResponse> images;
    private LocalDateTime createdAt;

}
