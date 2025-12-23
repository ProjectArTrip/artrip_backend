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

    private boolean isFavorite;
}

