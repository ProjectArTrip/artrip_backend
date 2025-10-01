package org.atdev.artrip.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.Enum.Genre;
import org.atdev.artrip.domain.Enum.Status;

@Data
@Builder
@AllArgsConstructor
public class ExhibitResponse {

    private Long id;
    private String title;
    private String description;

    private String startDate;
    private String endDate;

    private Status status;
    private String posterUrl;
    private String ticketUrl;
    private Genre genre;

    private Double latitude;
    private Double longitude;
}
