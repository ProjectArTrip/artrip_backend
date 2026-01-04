package org.atdev.artrip.converter;

import org.atdev.artrip.controller.dto.response.ExhibitHallListResponse;
import org.atdev.artrip.controller.dto.response.ExhibitHallResponse;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.springframework.stereotype.Component;

@Component
public class AdminExhibitHallConverter {

    public ExhibitHallListResponse toListResponse(ExhibitHall hall, long exhibitCount) {

        return ExhibitHallListResponse.builder()
                .exhibitHallId(hall.getExhibitHallId())
                .name(hall.getName())
                .country(hall.getCountry())
                .region(hall.getRegion())
                .phone(hall.getPhone())
                .isDomestic(hall.getIsDomestic())
                .exhibitCount(exhibitCount)
                .build();
    }

    public ExhibitHallResponse toResponse(ExhibitHall hall, long exhibitCount) {

        return ExhibitHallResponse.builder()
                .exhibitHallId(hall.getExhibitHallId())
                .name(hall.getName())
                .address(hall.getAddress())
                .country(hall.getCountry())
                .region(hall.getRegion())
                .phone(hall.getPhone())
                .homepageUrl(hall.getHomepageUrl())
                .openingHours(hall.getOpeningHours())
                .isDomestic(hall.getIsDomestic())
                .exhibitCount(exhibitCount)
                .closedDays(hall.getClosedDays())
                .latitude(hall.getLatitude())
                .longitude(hall.getLongitude())
                .build();
    }
}
