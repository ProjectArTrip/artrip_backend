package org.atdev.artrip.service.dto.command;

import lombok.Builder;
import org.atdev.artrip.constants.SortType;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
public record ExhibitFilterCommand (

        String query,

        LocalDate startDate,
        LocalDate endDate,
        Boolean isDomestic,
        String country,
        String region,

        Set<String> genres,
        Set<String> styles,
        SortType sortType,

        Long size,
        Long cursor,
        Long userId,

        Integer width,
        Integer height,
        String format

        ){

        public List<String> getValidSearch(){
                List<String> keywords = new ArrayList<>();

                if (StringUtils.hasText(this.query)) {
                        keywords.add(this.query.trim());
                }
                return keywords;
        }

}
