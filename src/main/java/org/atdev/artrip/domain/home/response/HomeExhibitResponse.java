package org.atdev.artrip.domain.home.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.atdev.artrip.domain.Enum.Status;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class HomeExhibitResponse {

    private Long exhibit_id;
    private String title;
    private String posterUrl;
    private Status status;
    private String country;
    private String region;
    private String startDate;
    private String endDate;
}

