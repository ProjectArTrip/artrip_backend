package org.atdev.artrip.utils;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record CursorPagination(
        Long cursor,

        @Min(value = 1, message = "최소값 1")
        @Max(value = 20, message = "최대값 20")
        Long size

) {
    public CursorPagination {
        if (size == null) {
            size = 20L;
        }
    }
}
