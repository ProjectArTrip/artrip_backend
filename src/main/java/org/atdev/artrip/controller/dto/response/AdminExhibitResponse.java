package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AdminExhibitResponse {

    private Long exhibitId;
    private String title;
    private String description;

    private ExhibitHallInfo exhibitHall;

    private LocalDate startDate;
    private LocalDate endDate;
    private String openingHours;

    private Status status;

    private String posterUrl;
    private String ticketUrl;

    private List<KeywordInfo> keywords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExhibitHallInfo {
        private Long exhibitHallId;
        private String name;
        private String address;
        private String country;
        private String region;
        private String phone;
        private String homepageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        private Long keywordId;
        private String name;
        private String type;  // GENRE, STYLE
    }

}
