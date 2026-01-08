package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.Status;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitSearchResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String exhibitHallName;

    private String startDate;
    private String endDate;

    private Status status;
    private String posterUrl;
    private Boolean isFavorite;

}