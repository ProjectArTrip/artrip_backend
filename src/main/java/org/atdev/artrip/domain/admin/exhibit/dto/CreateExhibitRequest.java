package org.atdev.artrip.domain.admin.exhibit.dto;

import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExhibitRequest {

    private String title;
    private String description;

    private Long exhibitHallId;
    private String exhibitHallName;
    private String address;
    private String country;
    private String region;
    private String phone;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String openingHours;

    private Status status;

    private String posterUrl; // 이미지 URL
    private String ticketUrl;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private List<Long> keywordIds;

}
