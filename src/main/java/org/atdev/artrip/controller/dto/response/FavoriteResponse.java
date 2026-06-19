package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.utils.StringUtils;

import java.time.LocalDate;

public record FavoriteResponse(
        Long favoriteId,
        Long exhibitId,
        String title,
        String posterUrl,
        Status status,
        boolean isFavorite,
        String exhibitPeriod,
        String hallName,
        String country,
        String region,
        LocalDate createdAt
) {

    public static FavoriteResponse from(FavoriteResult.FavoriteItem result) {
        return new FavoriteResponse(
                result.favoriteId(),
                result.exhibitId(),
                result.title(),
                StringUtils.emptyIfNull(result.posterUrl()),
                result.status(),
                result.isFavorite(),
                result.exhibitPeriod(),
                result.exhibitHallName(),
                result.country(),
                result.region(),
                result.createdAt()
        );
    }


}
