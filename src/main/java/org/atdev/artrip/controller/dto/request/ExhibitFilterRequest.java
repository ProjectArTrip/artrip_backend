package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitFilterRequest {

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isDomestic;

    private String country;
    private String region;

    private Set<String> genres;
    private Set<String> styles;

    private SortType sortType;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]*$")
    private String keyword;

    private Long cursor;

    private Long size;

    public Long getSize() {
        return size != null && size > 0 ? size : 20L;
    }

}
