package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;

import java.util.List;

@Builder
public record HomeListResponse(

        Long exhibitId,
        String title,
        String posterUrl,
        Status status,
        String exhibitPeriod,

        String hallName,
        String countryName,
        String regionName,

        boolean isFavorite) {
    public static HomeListResponse from(ExhibitRandomResult result) {
        return HomeListResponse.builder()

                .exhibitId(result.exhibitId())
                .title(result.title())
                .posterUrl(result.posterUrl())
                .status(result.status())
                .exhibitPeriod(result.exhibitPeriod())

                .hallName(result.hallName())
                .countryName(result.countryName())
                .regionName(result.regionName())

                .isFavorite(result.isFavorite())
                .build();
    }

    public static List<HomeListResponse> fromList(List<ExhibitRandomResult> results) {
        return results.stream()
                .map(HomeListResponse::from)
                .toList();
    }

    public static HomeListResponse from(ExhibitFilterResult.ExhibitItem item) {
        return HomeListResponse.builder()
                .exhibitId(item.exhibitId())
                .title(item.title())
                .posterUrl(item.posterUrl())
                .status(item.status())
                .exhibitPeriod(item.exhibitPeriod())
                .isFavorite(item.isFavorite())
                .hallName(item.hallName())
                .countryName(item.countryName())
                .regionName(item.regionName())
                .build();
    }
}

