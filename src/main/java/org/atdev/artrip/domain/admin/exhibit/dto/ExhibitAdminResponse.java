package org.atdev.artrip.domain.admin.exhibit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.domain.Enum.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExhibitAdminResponse {

    private Long exhibitId;
    private String title;
    private String description;

    private ExhibitHallInfo exhibitHall;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String openingHours;

    private Status status;

    private String posterUrl;
    private String ticketUrl;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private List<keywordInfo> keywords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 내부 클래스: 전시장 정보
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

    // 내부 클래스: 키워드 정보
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class keywordInfo {
        private Long keywordId;
        private String name;
        private String type;  // GENRE, STYLE
    }

}
