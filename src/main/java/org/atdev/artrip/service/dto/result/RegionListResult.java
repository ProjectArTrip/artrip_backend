package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.exhibit.Region;

import java.util.List;

public record RegionListResult(
        List<RegionResult> regions
) {

    public static RegionListResult from(List<Region> regions){

        List<RegionResult> results = regions.stream()
                .map(RegionResult::from)
                .toList();

        return new RegionListResult(results);
    }
}
