package org.atdev.artrip.service.social;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;

public interface SocialVerifier {

    Provider getProvider();
    SocialUserInfo verify(String idToken);
}
