package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.OverseasCountry;

import java.util.Arrays;
import java.util.List;

public record CountryResult (
        String label
) {

    public static CountryResult from(OverseasCountry country) {
        return new CountryResult(
                country.getLabel()
        );
    }

    public static List<CountryResult> from(){
        return Arrays.stream(OverseasCountry.values())
                .map(CountryResult::from)
                .toList();
    }
}
