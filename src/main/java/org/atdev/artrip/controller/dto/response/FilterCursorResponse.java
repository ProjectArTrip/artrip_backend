package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;

import java.util.List;

@Builder
public record FilterCursorResponse(
        List<HomeResponse> exhibits,
        boolean hasNext,
        Long nextCursor
) {
    public static FilterCursorResponse from(ExhibitFilterResult result) {

        return FilterCursorResponse.builder()
                .exhibits(result.items().stream()
                        .map(HomeResponse::from)
                        .toList())
                .hasNext(result.hasNext())
                .nextCursor(result.nextCursor())
                .build();
    }
}