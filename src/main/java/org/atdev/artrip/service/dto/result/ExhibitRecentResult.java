package org.atdev.artrip.service.dto.result;

import lombok.Builder;
import org.atdev.artrip.domain.exhibit.Exhibit;

@Builder
public record ExhibitRecentResult(
        Long exhibitId,
        String title,
        String exhibitHallName
) {

    public static ExhibitRecentResult from(Exhibit exhibit){

        return ExhibitRecentResult.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .exhibitHallName(exhibit.getExhibitHall().getName())
                .build();
    }

}
