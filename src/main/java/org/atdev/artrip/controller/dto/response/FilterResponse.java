package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;

import java.util.List;

@Builder
public record FilterResponse(
        List<HomeListResponse> exhibits,
        boolean hasNext,
        Long nextCursor
) {
    public static FilterResponse from(ExhibitFilterResult result) {

        return FilterResponse.builder()
                .exhibits(result.items().stream()
                        .map(HomeListResponse::from)
                        .toList())
                .hasNext(result.hasNext())
                .nextCursor(result.nextCursor())
                .build();
    }
}