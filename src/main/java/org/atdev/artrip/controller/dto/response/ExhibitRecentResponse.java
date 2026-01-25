package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;

import java.util.List;


@Builder
public record ExhibitRecentResponse (
        List<ExhibitRecentResult> exhibits
) {

    public static ExhibitRecentResponse from(List<ExhibitRecentResult> results) {
        return new ExhibitRecentResponse(results);
    }
}
