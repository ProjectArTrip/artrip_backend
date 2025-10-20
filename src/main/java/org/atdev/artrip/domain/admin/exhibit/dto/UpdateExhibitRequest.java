package org.atdev.artrip.domain.admin.exhibit.dto;

import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateExhibitRequest {

    private String title;
    private String description;

    private Long exhibitHallId;
    private String exhibitHallName;
    private String address;
    private String country;
    private String region;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String openingHours;

    private Status status;

    private String posterUrl;
    private String ticketUrl;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private List<Long> keywordIds;
}
