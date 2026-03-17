package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitMarkerListResult;

import java.util.List;

public record ExhibitMarkerListResponse(List<ExhibitMarkerResponse> markers) {

    public static ExhibitMarkerListResponse from(ExhibitMarkerListResult result) {
        return new ExhibitMarkerListResponse(
                result.results().stream()
                        .map(ExhibitMarkerResponse::from)
                        .toList()
        );
    }
}