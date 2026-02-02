package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.FavoriteResult;

import java.util.List;

@Builder
public record FavoriteListResponse(
        List<FavoriteResponse> favorites,
        boolean hasNext,
        Long nextCursor
) {
    public static FavoriteListResponse from(FavoriteResult result) {
        return FavoriteListResponse.builder()
                .favorites(result.items().stream()
                                .map(FavoriteResponse::from)
                                .toList())
                .hasNext(result.hasNext())
                .nextCursor(result.nextCursor())
                .build();
    }
}
