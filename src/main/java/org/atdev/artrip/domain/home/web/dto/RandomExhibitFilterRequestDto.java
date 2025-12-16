package org.atdev.artrip.domain.home.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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

    @NotEmpty(groups = GenreRandomGroup.class) // 장르별 랜덤 조회
    private String singleGenre;

    private Set<String> genres;
    private Set<String> styles;

    @NotNull(groups = ScheduleRandomGroup.class) // 이번주 전시 조회
    private LocalDate date;

    private Integer limit;

    @Schema(hidden = true)
    @AssertTrue(message = "국내 전시는 region 필수, 국외 전시는 country 필수입니다.",
    groups = {TodayRandomGroup.class,
            ScheduleRandomGroup.class,
            GenreRandomGroup.class,
            UserCustomGroup.class})
    public boolean isDomesticRegionCountryValid() {
        if (isDomestic == null) return true;

        if (isDomestic) {
            return region != null && !region.isBlank()
                    && (country == null || country.isBlank());
        } else {
            return country != null && !country.isBlank()
                    && (region == null || region.isBlank());
        }
    }

}