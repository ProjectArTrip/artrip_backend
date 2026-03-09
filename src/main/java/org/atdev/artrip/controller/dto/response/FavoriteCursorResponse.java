package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.FavoriteResult;

import java.util.List;

@Builder
public record FavoriteCursorResponse(
        List<FavoriteResponse> favorites,
        boolean hasNext,
        Long nextCursor
) {
    public static FavoriteCursorResponse from(FavoriteResult result) {
        return FavoriteCursorResponse.builder()
                .favorites(result.items().stream()
                                .map(FavoriteResponse::from)
                                .toList())
                .hasNext(result.hasNext())
                .nextCursor(result.nextCursor())
                .build();
    }
}
