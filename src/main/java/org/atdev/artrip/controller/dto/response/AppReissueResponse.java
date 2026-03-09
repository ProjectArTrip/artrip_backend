package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AppReissueResult;

public record AppReissueResponse(
        String newAccessToken,
        String refreshToken
) {
    public static AppReissueResponse from(AppReissueResult result){
        return new AppReissueResponse(result.newAccessToken(), result.refreshToken());
    }
}
