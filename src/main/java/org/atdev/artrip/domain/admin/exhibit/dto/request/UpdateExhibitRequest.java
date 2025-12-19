package org.atdev.artrip.domain.admin.exhibit.dto.request;

import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.time.LocalDate;
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
    private String phone;

    private LocalDate startDate;
    private LocalDate endDate;
    private String openingHours;

    private Status status;

    private String posterUrl;
    private String ticketUrl;

    private List<Long> keywordIds;
}
