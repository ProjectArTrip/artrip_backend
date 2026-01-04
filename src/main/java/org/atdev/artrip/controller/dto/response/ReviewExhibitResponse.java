package org.atdev.artrip.controller.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewExhibitResponse {

    private Long reviewId;
    private String Nickname;
    private LocalDate visitDate;
    private String content;
    private String thumbnailUrl;

}
