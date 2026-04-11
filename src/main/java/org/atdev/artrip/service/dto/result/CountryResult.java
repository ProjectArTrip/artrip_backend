package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.Country;

public record CountryResult (
        String label
) {

    public static CountryResult from(Country country) {
        return new CountryResult(
                country.getLabel()
        );
    }
}
