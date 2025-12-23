package org.atdev.artrip.domain.home.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.Enum.Status;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeListResponse {

    private Long exhibit_id;
    private String title;
    private String posterUrl;
    private Status status;
    private String exhibitPeriod;

    private String hallName;
    private String countryName;
    private String regionName;

    private boolean isFavorite;

    public HomeListResponse(Long exhibit_id, String title, String posterUrl, Status status,
                            String exhibitPeriod, String hallName, String countryName, String regionName) {
        this.exhibit_id = exhibit_id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.status = status;
        this.exhibitPeriod = exhibitPeriod;
        this.hallName = hallName;
        this.countryName = countryName;
        this.regionName = regionName;
        this.isFavorite = false;
    }
}

