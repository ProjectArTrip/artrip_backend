package org.atdev.artrip.service.dto.command;

import lombok.Builder;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

@Builder
public record FavoriteCondition(
        Long userId,
        String sortType,
        Boolean isDomestic,
        String region,
        String country,
        Long cursor,
        Long size
) {

    public FavoriteCondition {
        boolean hasRegion = region != null && !region.isBlank();
        boolean hasCountry = country != null && !country.isBlank();

        if ((hasRegion || hasCountry) && isDomestic == null) {
            throw new GeneralException(FavoriteErrorCode._REQUIRES_DOMESTIC);
        }

        if(Boolean.TRUE.equals(isDomestic) && hasCountry) {
            throw new GeneralException(FavoriteErrorCode._COUNTRY_REQUIRES_OVERSEAS);
        }
    }
}
