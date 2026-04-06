package org.atdev.artrip.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
        private String normalize(String value) {
                return (value == null || "전체".equals(value)) ? null : value;
        }

        @Schema(hidden = true)
        @AssertTrue(message = "국내 전시는 regions 필수(전체 가능), 국외 전시는 countries 필수(전체 가능)이며 둘을 동시에 보낼 수 없습니다.")
        public boolean isDomesticRegionCountryValid() {
                if (isDomestic == null) return true;

                boolean hasRegion = region != null && !region.isBlank();
                boolean hasCountry = country != null && !country.isBlank();

                if (isDomestic) {
                        return hasRegion && !hasCountry;
                } else {
                        return hasCountry && !hasRegion;
                }
        }

        public ExhibitSearchCondition toCommand(Long userId, CursorPagination cursorPagination) {
                return ExhibitSearchCondition.builder()
                        .query(this.query)
                        .startDate(this.startDate)
                        .endDate(this.endDate)
                        .isDomestic(this.isDomestic)
                        .country(normalize(this.country))
                        .region(normalize(this.region))
                        .genres(this.genres)
                        .styles(this.styles)
                        .sortType(this.sortType)
                        .userId(userId)
                        .cursor(cursorPagination.cursor())
                        .size(cursorPagination.size())
                        .build();

        }
}
