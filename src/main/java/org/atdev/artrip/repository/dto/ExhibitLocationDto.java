package org.atdev.artrip.repository.dto;

import java.math.BigDecimal;

public record ExhibitLocationDto(
        Long exhibitId,
        BigDecimal longitude,
        BigDecimal latitude
) {}
