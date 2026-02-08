package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitRandomListResult;

import java.util.List;

public record HomeListResponse(
        List<HomeResponse> exhibits
) {
    public static HomeListResponse from(ExhibitRandomListResult result) {

        return new HomeListResponse(
                result.results().stream()
                        .map(HomeResponse::from)
                        .toList()
        );
    }
}