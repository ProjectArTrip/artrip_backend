package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.ExhibitDetailResult;

import java.math.BigDecimal;

@Builder
public record ExhibitDetailResponse (
        Long exhibitId,
        String title,
        String description,
        String posterUrl,
        String ticketUrl,

        String exhibitPeriod,
        Status status,

        String hallName,
        String hallAddress,
        String hallOpeningHours,
        String hallPhone,
        BigDecimal hallLatitude,
        BigDecimal hallLongitude,

        boolean isFavorite
){
    public static ExhibitDetailResponse from(ExhibitDetailResult result) {
        return ExhibitDetailResponse.builder()
                .exhibitId(result.exhibitId())
                .title(result.title())
                .description(result.description())
                .posterUrl(result.posterUrl())
                .ticketUrl(result.ticketUrl())
                .exhibitPeriod(result.exhibitPeriod())
                .status(result.status())
                .hallName(result.hallName())
                .hallAddress(result.hallAddress())
                .hallOpeningHours(result.hallOpeningHours())
                .hallPhone(result.hallPhone())
                .hallLatitude(result.hallLatitude())
                .hallLongitude(result.hallLongitude())
                .isFavorite(result.isFavorite())
                .build();
    }
}
