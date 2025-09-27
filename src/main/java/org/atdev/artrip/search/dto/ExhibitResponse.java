package org.atdev.artrip.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExhibitResponse {

    private Long id;
    private String title;
    private String description;

    private String startDate;
    private String endDate;

    private String status;
    private String posterUrl;
    private String ticketUrl;
    private String genre;

    private Double latitude;
    private Double longitude;
}
