package org.atdev.artrip.controller.dto.request;

import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record ExhibitFilterRequest (
        LocalDate startDate,
        LocalDate endDate,

        Boolean isDomestic,

        String country,
        String region,

        Set<String> genres,
        Set<String> styles,

        SortType sortType) {

}
