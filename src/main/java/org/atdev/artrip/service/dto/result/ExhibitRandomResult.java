package org.atdev.artrip.service.dto.result;

import lombok.Builder;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.utils.DateTimeUtils;

@Builder
public record ExhibitRandomResult(
        Long exhibitId,
        String title,
        String posterUrl,
        Status status,

        String exhibitPeriod,

        String hallName,
        String countryName,
        String regionName,

        boolean isFavorite,
        String resizeUrl
) {
    public static ExhibitRandomResult of(Exhibit exhibit, boolean isFavorite,String resizedUrl){

        return ExhibitRandomResult.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .exhibitPeriod(DateTimeUtils.convertDate(exhibit.getStartDate(), exhibit.getEndDate()))
                .status(exhibit.getStatus())

                .hallName(exhibit.getExhibitHall().getName())
                .countryName(exhibit.getExhibitHall().getCountry())
                .regionName(exhibit.getExhibitHall().getRegion())

                .isFavorite(isFavorite)
                .resizeUrl(resizedUrl)
                .build();
    }

    public ExhibitRandomResult withFavorite(boolean favorite) {
        return ExhibitRandomResult.builder()
                .exhibitId(this.exhibitId)
                .title(this.title)
                .posterUrl(this.posterUrl)
                .status(this.status)
                .exhibitPeriod(this.exhibitPeriod)
                .hallName(this.hallName)
                .countryName(this.countryName)
                .regionName(this.regionName)
                .isFavorite(favorite)
                .resizeUrl(this.resizeUrl)
                .build();
    }
}
