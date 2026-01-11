package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CursorPaginationResponse<T> {

    private List<T> data;
    private boolean hasNext;
    private Long nextCursor;

    public static <T> CursorPaginationResponse<T> of(List<T> data, boolean hasNext, Long nextCursor) {
        return CursorPaginationResponse.<T>builder()
                .data(data)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }
}
