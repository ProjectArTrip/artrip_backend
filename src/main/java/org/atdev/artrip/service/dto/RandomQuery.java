package org.atdev.artrip.service.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RandomQuery(
        boolean isDomestic,
        String region,
        String country,
        LocalDate date,
        String singleGenre,
        Long userId,

        Integer width,
        Integer height,
        String format
) {
}

