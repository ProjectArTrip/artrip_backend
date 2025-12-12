package org.atdev.artrip.domain.exhibit.web.dto;

import lombok.*;

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

    private LocalDateTime betweenDate; // start <= date <= end 조건

    private Integer limit; // random limit
}