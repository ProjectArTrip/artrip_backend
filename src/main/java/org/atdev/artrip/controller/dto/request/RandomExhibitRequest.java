package org.atdev.artrip.controller.dto.request;

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
    private String singleGenre;
    private Set<String> genres;
    private Set<String> styles;
    private int limit;
}
