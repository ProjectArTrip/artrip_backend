package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.OverseasCountry;

import java.util.Arrays;
import java.util.List;

public record CountryListResult(
        List<CountryResult> countries
) {

    public static CountryListResult from(){

        List<CountryResult> results = Arrays.stream(OverseasCountry.values())
                .map(CountryResult::from)
                .toList();

        return new CountryListResult(results);
    }
}
