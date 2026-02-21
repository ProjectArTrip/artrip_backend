package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.result.FavoriteResult;

import java.time.LocalDate;

public record FavoriteResponse(
        Long favoriteId,
        Long exhibitId,
        String title,
        String posterUrl,
        Status exhibitStatus,
        boolean active,
        String exhibitPeriod,
        String exhibitHallName,
        String country,
        String region,
        LocalDate createdAt
) {

    public static FavoriteResponse from(FavoriteResult.FavoriteItem result) {
        return new FavoriteResponse(
                result.favoriteId(),
                result.exhibitId(),
                result.title(),
                result.posterUrl(),
                result.status(),
                result.active(),
                result.exhibitPeriod(),
                result.exhibitHallName(),
                result.country(),
                result.region(),
                result.createdAt()
        );
    }


}
