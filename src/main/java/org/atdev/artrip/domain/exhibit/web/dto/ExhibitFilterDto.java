package org.atdev.artrip.domain.exhibit.web.dto;

import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.Enum.SortType;
import org.atdev.artrip.domain.keyword.data.Keyword;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class ExhibitFilterDto {

    private LocalDate startDate;
    private LocalDate endDate;

    private String style;

    private String type;    // 국내,해외
    private String country;
    private String region;

    private Set<String> genres;
    private Set<String> styles;

    private SortType sortType;

}
