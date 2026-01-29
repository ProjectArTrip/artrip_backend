package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.RegionResult;

import java.util.List;

@Builder
public record RegionResponse(
        String region,
        String imageUrl) {


    public static RegionResponse from(RegionResult result) {

        return new RegionResponse(
                result.region(),
                result.imageUrl()
        );
    }
    public static List<RegionResponse> from(List<RegionResult> results) {

        if (results == null) return List.of();

        return results.stream()
                .map(RegionResponse::from)
                .toList();
    }

}