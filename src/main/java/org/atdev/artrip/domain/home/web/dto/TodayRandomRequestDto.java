package org.atdev.artrip.domain.home.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayRandomRequestDto {

    @NotNull
    private Boolean isDomestic;

    private String region;
    private String country;

    @Schema(hidden = true)
    @AssertTrue(message = "국내 전시는 region 필수(전체 가능), 국외 전시는 country 필수(전체 가능)이며 둘을 동시에 보낼 수 없습니다.")
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

