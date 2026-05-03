package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.OnboardingStep;
import org.atdev.artrip.service.dto.result.SocialLoginResult;

public record SocialLoginResponse(
        String accessToken,
        String refreshToken,
        OnboardingStep onboardingStep
) {
    public static SocialLoginResponse from(SocialLoginResult result) {
        return new SocialLoginResponse(result.accessToken(), result.refreshToken(), result.onboardingStep());
    }
}
