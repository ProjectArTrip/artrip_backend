package org.atdev.artrip.domain.exhibit.reponse;

import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;

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
