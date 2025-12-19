package org.atdev.artrip.domain.admin.exhibit.dto.request;

import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateExhibitRequest {

    private Long exhibitHallId;
    private String title;
    private String description;
    private String ticketUrl;
    private String posterUrl;

    private LocalDate startDate;
    private LocalDate endDate;

    private Status status;

    private List<Long> keywordIds;

}
