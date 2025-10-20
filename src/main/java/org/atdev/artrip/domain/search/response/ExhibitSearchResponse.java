package org.atdev.artrip.domain.search.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ExhibitSearchResponse {

    private Long id;
    private String title;
    private String description;

    private String startDate;
    private String endDate;

    private Status status;
    private String posterUrl;
    private String ticketUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
