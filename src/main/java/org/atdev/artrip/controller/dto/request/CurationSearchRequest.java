package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.Country;
import org.atdev.artrip.service.dto.condition.CurationSearchCondition;

public record CurationSearchRequest(
        Boolean domestic,
        String country
) {

    public CurationSearchRequest {
        country = (country == null || country.isBlank() || Country.ALL.getLabel().equals(country)) ? null : country;
    }
    public CurationSearchCondition toCondition() {
        return new CurationSearchCondition(domestic, country);
    }
}
