package org.atdev.artrip.service.dto.condition;

public record CurationSearchCondition(
        Boolean domestic,
        String region,
        String country
) {
}
