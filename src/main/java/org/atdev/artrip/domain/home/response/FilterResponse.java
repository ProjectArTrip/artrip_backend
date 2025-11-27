package org.atdev.artrip.domain.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FilterResponse {

    private List<HomeListResponse> exhibits;
    private boolean hasNext;
    private Long nextCursor;

}