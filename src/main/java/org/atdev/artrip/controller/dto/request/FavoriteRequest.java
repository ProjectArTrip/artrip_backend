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

        return FavoriteCondition.builder()
                .userId(userId)
                .sortType(this.sortType)
                .isDomestic(isDomestic)
                .region(region)
                .country(country)
                .cursor(cursor)
                .size(size)
                .build();
    }
}
