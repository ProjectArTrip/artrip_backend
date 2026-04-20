package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.Country;
import org.atdev.artrip.service.dto.condition.CurationSearchCondition;

public record CurationSearchRequest(
        Boolean domestic,
        String region,
        String country
) {

    public CurationSearchRequest {
        region  = (region  == null || region.isBlank()  || "전체".equals(region))                 ? null : region;
        country = (country == null || country.isBlank() || Country.ALL.getLabel().equals(country)) ? null : country;
    }
    public CurationSearchCondition toCondition() {
        return new CurationSearchCondition(domestic, region, country);
    }
}
