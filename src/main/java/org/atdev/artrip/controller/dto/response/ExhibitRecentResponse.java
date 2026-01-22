package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;

import java.util.List;

@Builder
public record ExhibitRecentResponse (
        Long exhibitId,
        String title,
        String exhibitHallName
){

    public static ExhibitRecentResponse from(ExhibitRecentResult result){
        return ExhibitRecentResponse.builder()
                .exhibitId(result.exhibitId())
                .title(result.title())
                .exhibitHallName(result.exhibitHallName())
                .build();
    }

    public static List<ExhibitRecentResponse> from(List<ExhibitRecentResult> results){
        return results.stream()
                .map(ExhibitRecentResponse::from)
                .toList();
    }
}
