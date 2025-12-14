package org.atdev.artrip.domain.home.web.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomExhibitRequest {

    private Boolean isDomestic;
    private String country;
    private String region;
    private LocalDate date;
    private Set<String> genres;
    private Set<String> styles;
    private int limit;
}
