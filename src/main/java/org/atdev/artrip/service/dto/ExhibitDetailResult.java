package org.atdev.artrip.service.dto;

import lombok.Builder;
import org.atdev.artrip.domain.exhibit.Exhibit;

@Builder
public record ExhibitDetailResult(
        Exhibit exhibit,
        boolean isFavorite,
        String resizedUrl
) {
}