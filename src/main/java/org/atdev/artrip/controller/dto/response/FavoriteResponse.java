package org.atdev.artrip.controller.dto.response;

import lombok.*;
import org.atdev.artrip.constants.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long favoriteId;
    private Long exhibitId;
    private String title;
    private String posterUrl;
    private Status exhibitStatus;
    private boolean active;
    private String exhibitPeriod;
    private String exhibitHallName;
    private String country;
    private String region;
    private LocalDateTime createdAt;

}
