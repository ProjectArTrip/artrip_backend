package org.atdev.artrip.domain.admin.exhibitHall.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExhibitHallResponse {

    private Long exhibitHallId;
    private String name;
    private String address;
    private String country;
    private String region;
    private String phone;
    private String homepageUrl;
    private String openingHours;
    private Boolean isDomestic;
    private Long exhibitCount;
    private String closedDays;

}
