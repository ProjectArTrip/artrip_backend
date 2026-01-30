package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.command.ExhibitSearchCondition;

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

        public ExhibitSearchCondition toCommand(Long userId, Long cursor, Long size, ImageResizeRequest resize) {
                return ExhibitSearchCondition.builder()
                        .query(this.query)
                        .startDate(this.startDate)
                        .endDate(this.endDate)
                        .isDomestic(this.isDomestic)
                        .country(this.country)
                        .region(this.region)
                        .genres(this.genres)
                        .styles(this.styles)
                        .sortType(this.sortType)
                        .userId(userId)
                        .cursor(cursor)
                        .size(size)
                        .width(resize.w())
                        .height(resize.h())
                        .format(resize.f())
                        .build();
        }
