package org.atdev.artrip.domain.home.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.atdev.artrip.domain.home.web.validationgroup.GenreRandomGroup;
import org.atdev.artrip.domain.home.web.validationgroup.ScheduleRandomGroup;
import org.atdev.artrip.domain.home.web.validationgroup.TodayRandomGroup;
import org.atdev.artrip.domain.home.web.validationgroup.UserCustomGroup;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomExhibitFilterRequestDto {

    @NotNull(groups = {
            TodayRandomGroup.class,
            ScheduleRandomGroup.class,
            GenreRandomGroup.class,
            UserCustomGroup.class
    })
    private Boolean isDomestic;

    private String country;
    private String region;

    @NotEmpty(groups = GenreRandomGroup.class)
    private Set<String> genres;

    @NotEmpty(groups = GenreRandomGroup.class)
    private Set<String> styles;

    @NotNull(groups = ScheduleRandomGroup.class)
    private LocalDate date;

    private Integer limit;
}