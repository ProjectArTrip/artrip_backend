package org.atdev.artrip.controller.dto.response;


import org.atdev.artrip.service.dto.result.CountryListResult;

public record CountryListResponse(
        CountryListResult result
) {

    public static CountryListResponse from(CountryListResult result){
        return new CountryListResponse(result);
    }


}
