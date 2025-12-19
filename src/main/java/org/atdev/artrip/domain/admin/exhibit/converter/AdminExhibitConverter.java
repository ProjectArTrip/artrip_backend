package org.atdev.artrip.domain.admin.exhibit.converter;

import org.atdev.artrip.domain.admin.exhibit.dto.response.AdminExhibitResponse;
import org.atdev.artrip.domain.admin.exhibit.dto.response.AdminExhibitListResponse;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdminExhibitConverter {

    public AdminExhibitListResponse toListResponse(Exhibit exhibit) {
        return AdminExhibitListResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .exhibitHallName(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getName() : null)
                .country(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getCountry() : null)
                .keywordCount(exhibit.getKeywords().size())
                .createdAt(exhibit.getCreatedAt())
                .updatedAt(exhibit.getUpdatedAt())
                .build();
    }

    public AdminExhibitResponse toAdminResponse(Exhibit exhibit) {
        return AdminExhibitResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .exhibitHall(toExhibitHallInfo(exhibit.getExhibitHall()))
                .openingHours(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getOpeningHours() : null)
                .status(exhibit.getStatus())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .keywords(exhibit.getKeywords().stream()
                        .map(this::toKeywordInfo)
                        .collect(Collectors.toList()))
                .createdAt(exhibit.getCreatedAt())
                .updatedAt(exhibit.getUpdatedAt())
                .build();
    }

    private AdminExhibitResponse.ExhibitHallInfo toExhibitHallInfo(ExhibitHall hall) {
        if (hall == null) return null;

        return AdminExhibitResponse.ExhibitHallInfo.builder()
                .exhibitHallId(hall.getExhibitHallId())
                .name(hall.getName())
                .address(hall.getAddress())
                .country(hall.getCountry())
                .region(hall.getRegion())
                .phone(hall.getPhone())
                .homepageUrl(hall.getHomepageUrl())
                .build();
    }

    private AdminExhibitResponse.KeywordInfo toKeywordInfo(Keyword keyword) {
        return AdminExhibitResponse.KeywordInfo.builder()
                .keywordId(keyword.getKeywordId())
                .name(keyword.getName())
                .type(keyword.getType().name())
                .build();
    }
}
