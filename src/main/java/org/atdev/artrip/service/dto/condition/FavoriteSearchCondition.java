package org.atdev.artrip.service.dto.condition;

import org.atdev.artrip.constants.SortType;

public record FavoriteSearchCondition(
        SortType sortType,
        String region,
        String country
) {
}
