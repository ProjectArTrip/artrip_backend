package org.atdev.artrip.domain.exhibit.web.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomExhibitFilter {

    private Boolean isDomestic;
    private String country;
    private String region;

    private Set<String> genres;
    private Set<String> styles;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer limit;
}