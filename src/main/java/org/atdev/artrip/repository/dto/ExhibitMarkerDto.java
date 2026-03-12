package org.atdev.artrip.repository.dto;

import java.math.BigDecimal;

public record ExhibitMarkerDto(
        Long exhibitId,
        BigDecimal longitude,
        BigDecimal latitude
) {}
