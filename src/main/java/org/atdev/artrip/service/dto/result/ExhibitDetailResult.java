package org.atdev.artrip.service.dto.result;

import lombok.Builder;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.utils.DateTimeUtils;

import java.math.BigDecimal;

@Builder
public record ExhibitDetailResult(

        Long exhibitId,
        String title,
        String description,
        String posterUrl,
        String exhibitPeriod,
        String ticketUrl,
        Status status,

        String hallName,
        String hallAddress,
        String hallOpeningHours,
        String hallPhone,
        BigDecimal hallLatitude,
        BigDecimal hallLongitude,
        boolean isFavorite,
        String resizedUrl
) {
    public static ExhibitDetailResult of(Exhibit exhibit, boolean isFavorite, String resizedUrl){
        return ExhibitDetailResult.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .posterUrl(exhibit.getPosterUrl())
                .exhibitPeriod(DateTimeUtils.convertDate(exhibit.getStartDate(), exhibit.getEndDate()))
                .ticketUrl(exhibit.getTicketUrl())
                .status(exhibit.getStatus())

                .hallName(exhibit.getExhibitHall().getName())
                .hallAddress(exhibit.getExhibitHall().getAddress())
                .hallOpeningHours(exhibit.getExhibitHall().getOpeningHours())
                .hallPhone(exhibit.getExhibitHall().getPhone())

                .hallLatitude(exhibit.getExhibitHall().getLatitude())
                .hallLongitude(exhibit.getExhibitHall().getLongitude())

                .isFavorite(isFavorite)
                .resizedUrl(resizedUrl)
                .build();
    }


}
