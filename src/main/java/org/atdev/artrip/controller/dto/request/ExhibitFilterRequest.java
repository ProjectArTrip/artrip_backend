package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.command.ExhibitFilterCommand;

import java.time.LocalDate;
import java.util.Set;

public record ExhibitFilterRequest (

        String query,

        LocalDate startDate,
        LocalDate endDate,

        Boolean isDomestic,

        String country,
        String region,

        Set<String> genres,
        Set<String> styles,

        SortType sortType
        ) {

        public ExhibitFilterCommand toCommand(Long userId, Long cursor, Long size, Integer width, Integer height, String format){
                return new ExhibitFilterCommand(
                        query,
                        startDate,
                        endDate,
                        isDomestic,
                        country,
                        region,
                        genres,
                        styles,
                        sortType,
                        size,
                        cursor,
                        userId,
                        width,
                        height,
                        format
                );
        }
}
