package org.atdev.artrip.service.dto.result;


public record SocialLoginResult(
        String accessToken,
        String refreshToken,
        boolean isFirstLogin
) {
    public static SocialLoginResult of(String accessToken,
                                       String refreshToken,
                                       boolean isFirstLogin){

        return new SocialLoginResult(accessToken, refreshToken, isFirstLogin);
    }

}
