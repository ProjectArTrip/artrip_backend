package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.command.FavoriteCondition;

public record FavoriteRequest(
        String sortType,
        String region,
        String country,
        Boolean isDomestic
) {
    public FavoriteCondition toCommand(Long userId, Long cursor, Long size) {

        SortType validatedSortType = (this.sortType == null || this.sortType.isBlank())
                ? SortType.LATEST
                : switch (this.sortType.toLowerCase()) {
            case "latest" -> SortType.LATEST;
            case "ending_soon" -> SortType.ENDING_SOON;
            case "popular" -> throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
            default -> throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
        };

        boolean hasRegion = region != null && !region.isBlank();
        boolean hasCountry = country != null && !country.isBlank();

        if (hasCountry || hasRegion) {
            if (isDomestic == null) {
                if(hasCountry) {
                    throw new GeneralException(FavoriteErrorCode._REQUIRES_DOMESTIC);
                } else {
                    throw new GeneralException(FavoriteErrorCode._REQUIRES_DOMESTIC);
                }
            }
        }

        return FavoriteCondition.builder()
                .userId(userId)
                .sortType(validatedSortType)
                .isDomestic(isDomestic)
                .region(region)
                .country(country)
                .cursor(cursor)
                .size(size)
                .build();
    }
}
