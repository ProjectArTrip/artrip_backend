package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.constants.Status;

@Getter
@Builder
public class ExhibitDetailResponse {

    private Long exhibitId;
    private String title;
    private String description;
    private String posterUrl;
    private String ticketUrl;

    private String exhibitPeriod;
    private Status status;

    private String hallName;
    private String hallAddress;
    private String hallOpeningHours;
    private String hallPhone;
    private Double hallLatitude;
    private Double hallLongitude;

    private boolean isFavorite;
}
