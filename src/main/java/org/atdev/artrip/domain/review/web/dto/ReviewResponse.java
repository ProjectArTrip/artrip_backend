package org.atdev.artrip.domain.review.web.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RestController;

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
    private List<String> imageUrls;
    private LocalDateTime createdAt;

}
