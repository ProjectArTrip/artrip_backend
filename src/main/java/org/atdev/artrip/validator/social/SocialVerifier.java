package org.atdev.artrip.validator.social;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import java.util.Map;
import java.util.HashMap;

public interface SocialVerifier {

    Provider getProvider();
    SocialUserInfo verify(String idToken);
    default Map<String, String> exchangeCodeForTokens(String authorizationCode) {
        return new HashMap<>();
    }
    void unlink(String providerId, String refreshToken);

}
