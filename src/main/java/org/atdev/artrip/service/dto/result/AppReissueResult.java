package org.atdev.artrip.service.dto.result;

public record AppReissueResult(
        String newAccessToken,
        String refreshToken
) {

    public static AppReissueResult of(String newAccessToken,String refreshToken){
        return new AppReissueResult(newAccessToken, refreshToken);
    }
}
