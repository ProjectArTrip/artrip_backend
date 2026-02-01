package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import org.atdev.artrip.service.dto.result.RegionListResult;

@Builder
public record RegionListResponse(
        RegionListResult result
) {


    public static RegionListResponse from(RegionListResult result){

        return new RegionListResponse(result);
    }

}