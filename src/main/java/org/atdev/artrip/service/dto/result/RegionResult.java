package org.atdev.artrip.service.dto.result;

import lombok.Builder;
import org.atdev.artrip.domain.exhibit.Region;

@Builder
public record RegionResult(
        String region,
        String imageUrl
) {
    public static RegionResult from(Region region) {
        return new RegionResult(region.getName(), region.getImageUrl());
    }
}