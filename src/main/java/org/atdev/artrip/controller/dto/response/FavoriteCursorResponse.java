package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.FavoriteResult;

import java.util.List;

public record FavoriteCursorResponse(
        List<FavoriteResponse> favorites,
        long favoriteTotalCount,
        boolean hasNext,
        Long nextCursor
) {

    public static FavoriteCursorResponse from(FavoriteResult result) {
        return new FavoriteCursorResponse(
                result.items().stream()
                        .map(FavoriteResponse::from)
                        .toList(),
                result.totalCount(),
                result.hasNext(),
                result.nextCursor()
        );
    }
}
