package org.atdev.artrip.domain.favortie.dto;

import lombok.*;
import org.atdev.artrip.domain.Enum.Status;

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
    private Status status;
    private String exhibitPeriod;
    private String exhibitHallName;
    private String country;
    private String region;
    private LocalDateTime createdAt;

}
