package org.atdev.artrip.domain.admin.exhibit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.domain.Enum.Status;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitListResponse {

    private Long exhibitId;
    private String title;
    private String posterUrl; // 이미지 URL
    private Status status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String exhibitHallName;
    private String country;

    private Integer keywordCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
