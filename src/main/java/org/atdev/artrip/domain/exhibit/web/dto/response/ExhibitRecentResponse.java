package org.atdev.artrip.domain.exhibit.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ExhibitRecentResponse {

    private Long exhibitId;
    private String title;
    private String exhibitHallName;
}
