package org.atdev.artrip.domain.admin.exhibit.dto;

import lombok.Data;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExhibitRequest {

    private Long exhibitHallId;
    private String title;
    private String description;
    private String ticketUrl;
    private String posterUrl;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Status status;

    private List<Long> keywordIds;

}
