package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.repository.dto.ExhibitMarkerDto;

import java.math.BigDecimal;
import java.util.List;

public record ExhibitMarkerListResult(
        List<ExhibitMarkerResult> results
) {
    public record ExhibitMarkerResult(
            Long id,
            BigDecimal latitude,
            BigDecimal longitude
    ) {}

    public static ExhibitMarkerListResult from(List<ExhibitMarkerDto> dtos) {
        return new ExhibitMarkerListResult(
                dtos.stream()
                        .map(dto -> new ExhibitMarkerResult(
                                dto.exhibitId(),
                                dto.latitude(),
                                dto.longitude()
                        ))
                        .toList()
        );
    }
}