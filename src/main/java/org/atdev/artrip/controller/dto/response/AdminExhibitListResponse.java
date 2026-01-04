package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminExhibitListResponse {

    private Long exhibitId;
    private String title;
    private String posterUrl; // 이미지 URL
    private Status status;

    private LocalDate startDate;
    private LocalDate endDate;

    private String exhibitHallName;
    private String country;

    private Integer keywordCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
