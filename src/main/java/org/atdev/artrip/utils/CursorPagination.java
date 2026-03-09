package org.atdev.artrip.utils;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CursorPagination {

    private Long cursor;

    @Min(value = 1, message = "최소값 1")
    @Max(value = 20, message = "최대값 20")
    private Long size = 20L;

    public static CursorPagination of(Long cursor, Long size) {
        return new CursorPagination(cursor, size);
    }
}
