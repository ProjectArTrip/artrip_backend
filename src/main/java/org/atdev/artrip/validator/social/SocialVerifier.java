package org.atdev.artrip.validator.social;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;

public interface SocialVerifier {

    Provider getProvider();
    SocialUserInfo verify(String idToken);
    default String fetchRefreshToken(String authorizationCode) { return null; }
    void unlink(String providerId, String refreshToken);

}
