package org.atdev.artrip.domain.admin.exhibitHall.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateExhibitHallRequest {

    private String name;
    private String address;
    private String country;
    private String region;
    private String phone;
    private String homepageUrl;
    private String openingHours;
    private Boolean isDomestic;
    private String closedDays;

}
