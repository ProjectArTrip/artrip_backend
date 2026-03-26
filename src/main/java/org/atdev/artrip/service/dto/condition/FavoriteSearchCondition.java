package org.atdev.artrip.service.dto.condition;

import org.atdev.artrip.constants.SortType;

import java.util.List;

public record FavoriteSearchCondition(
        SortType sortOption,
        List<String> regions,
        List<String> countries
) {
}
