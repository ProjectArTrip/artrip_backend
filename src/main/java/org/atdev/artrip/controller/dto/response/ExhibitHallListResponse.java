package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExhibitHallListResponse {

    private Long exhibitHallId;
    private String name;
    private String country;
    private String region;
    private String phone;
    private Boolean isDomestic;
    private Long exhibitCount;
    private String closedDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
