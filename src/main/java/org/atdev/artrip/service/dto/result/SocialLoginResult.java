package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.OnboardingStep;

public record SocialLoginResult(
        String accessToken,
        String refreshToken,
        OnboardingStep onboardingStep
) {
    public static SocialLoginResult of(String accessToken,
                                       String refreshToken,
                                       OnboardingStep onboardingStep) {
        return new SocialLoginResult(accessToken, refreshToken, onboardingStep);
    }
}
