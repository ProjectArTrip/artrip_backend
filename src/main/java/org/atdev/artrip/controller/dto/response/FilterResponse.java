package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FilterResponse<T> {

    private List<T> result;
    private boolean hasNext;
    private Long nextCursor;

}