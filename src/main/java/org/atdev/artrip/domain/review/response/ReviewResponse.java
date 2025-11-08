package org.atdev.artrip.domain.review.response;

import lombok.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private LocalDate date;
    private String content;
    private List<String> imageUrls;

}
