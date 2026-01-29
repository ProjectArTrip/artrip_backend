package org.atdev.artrip.controller.dto.response;


import org.atdev.artrip.service.dto.result.CountryResult;

import java.util.List;

public record CountryResponse(
        String country
) {

    public static CountryResponse from(CountryResult result){

        return new CountryResponse(result.label());
    }

    public static List<CountryResponse> from(List<CountryResult> results){
        return results.stream()
                .map(CountryResponse::from)
                .toList();
    }

}
