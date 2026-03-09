package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.OverseasCountry;

public record CountryResult (
        String label
) {

    public static CountryResult from(OverseasCountry country) {
        return new CountryResult(
                country.getLabel()
        );
    }
}
