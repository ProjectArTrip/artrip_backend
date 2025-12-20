package org.atdev.artrip.domain.exhibit.web.dto.request;

import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.Enum.SortType;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class ExhibitFilterRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isDomestic;

    private String country;
    private String region;

    private Set<String> genres;
    private Set<String> styles;

    private SortType sortType;

}
