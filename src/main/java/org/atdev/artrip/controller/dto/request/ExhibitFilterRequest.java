package org.atdev.artrip.controller.dto.request;

import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class ExhibitFilterRequest {

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isDomestic;

    private String country;
    private String region;

    private Set<String> genres;
    private Set<String> styles;

    private SortType sortType;

}
