package org.atdev.artrip.controller.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateExhibitHallRequest {

    private String name;
    private String address;
    private String country;
    private String region;
    private String phone;
    private String homepageUrl;
    private String openingHours;
    private Boolean isDomestic;
    private String closedDays;
    private BigDecimal longitude;
    private BigDecimal latitude;
}
