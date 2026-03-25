package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.condition.ExhibitSearchCondition;
import org.atdev.artrip.utils.CursorPagination;

import java.time.LocalDate;
import java.util.Set;

public record ExhibitFilterRequest (

        String query,

        LocalDate startDate,
        LocalDate endDate,

        Boolean isDomestic,

        String country,
        String region,

        Set<String> genres,
        Set<String> styles,

        SortType sortType
        ) {

        public ExhibitSearchCondition toCommand(Long userId, CursorPagination cursorPagination) {
                return ExhibitSearchCondition.builder()
                        .query(this.query)
                        .startDate(this.startDate)
                        .endDate(this.endDate)
                        .isDomestic(this.isDomestic)
                        .country(this.country)
                        .region(this.region)
                        .genres(this.genres)
                        .styles(this.styles)
                        .sortType(this.sortType)
                        .userId(userId)
                        .cursor(cursorPagination.cursor())
                        .size(cursorPagination.size())
                        .build();

        }
}
