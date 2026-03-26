package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ExhibitMarkerListResult.ExhibitMarkerResult;
import java.math.BigDecimal;

public record ExhibitMarkerResponse(
        Long id,
        BigDecimal lat,
        BigDecimal lng
) {
    public static ExhibitMarkerResponse from(ExhibitMarkerResult result) {
        return new ExhibitMarkerResponse(
                result.id(),
                result.latitude(),
                result.longitude()
        );
    }
}