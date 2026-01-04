package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.elastic.document.KeywordInfo;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExhibitSearchResponse {

    private Long id;
    private String title;
    private String description;

    private String startDate;
    private String endDate;

    private Status status;
    private String posterUrl;
    private String ticketUrl;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private List<KeywordInfo> keywords;
}