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

    @Schema(hidden = true)
    private Set<String> genres;
    @Schema(hidden = true)
    private Set<String> styles;

    @NotNull(groups = ScheduleRandomGroup.class) // 이번주 전시 조회
    private LocalDate date;

    @Schema(hidden = true)
    private Integer limit;

    @Schema(hidden = true)
    @AssertTrue(message = "국내 전시는 region 필수(전체 가능), 국외 전시는 country 필수(전체 가능)이며 둘을 동시에 보낼 수 없습니다.",
    groups = {TodayRandomGroup.class,
            ScheduleRandomGroup.class,
            GenreRandomGroup.class,
            UserCustomGroup.class})
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

}