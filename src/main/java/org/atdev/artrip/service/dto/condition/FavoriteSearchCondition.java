package org.atdev.artrip.service.dto.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import org.atdev.artrip.constants.SortType;

import java.util.List;

public record FavoriteSearchCondition(
        SortType sortType,
        List<String> regions,
        List<String> countries
) {
}
