package org.atdev.artrip.service.event;

import org.atdev.artrip.constants.Provider;

import java.util.List;

public record WithdrawEvent(
        String accessToken,
        String refreshToken,
        List<SocialInfo> socials
) {
    public record SocialInfo(Provider provider, String providerId, String refreshToken) {}
}
