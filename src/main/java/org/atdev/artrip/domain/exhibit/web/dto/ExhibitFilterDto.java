package org.atdev.artrip.domain.exhibit.web.dto;

import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.keyword.data.Keyword;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class ExhibitFilterDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private String region;
    private String style;
    private Keyword keyword;

    private String type;
    private String country;
    private Set<String> genres;
    private Set<String> styles;

}
