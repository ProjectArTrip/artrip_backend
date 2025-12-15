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

    private String hall_Name;
    private String hall_Address;
    private String hall_OpeningHours;
    private String hall_Phone;
    private BigDecimal hall_latitude;
    private BigDecimal hall_longitude;
}
