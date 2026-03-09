package org.atdev.artrip.service.dto.condition;

import lombok.Builder;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record ExhibitSearchCondition(

        String query,

        LocalDate startDate,
        LocalDate endDate,
        Boolean isDomestic,
        String country,
        String region,

        Set<String> genres,
        Set<String> styles,
        SortType sortType,

        Long size,
        Long cursor,
        Long userId
        ){
}
