package org.atdev.artrip.domain.review.web.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListResponse {

    private Long reviewId;
    private String reviewTitle;
    private LocalDate visitDate;
    private String content;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}
