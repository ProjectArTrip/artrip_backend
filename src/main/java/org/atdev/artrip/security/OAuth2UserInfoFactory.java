package org.atdev.artrip.security;

import org.atdev.artrip.domain.oauth.OAuth2UserInfo;
import org.atdev.artrip.domain.oauth.AppleUserInfo;
import org.atdev.artrip.domain.oauth.GoogleUserInfo;
import org.atdev.artrip.domain.oauth.KakaoUserInfo;
import org.atdev.artrip.constants.Provider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(Provider provider, Map<String, Object> attributes) {
        switch (provider) {
            case KAKAO: return new KakaoUserInfo(attributes);
            case GOOGLE: return new GoogleUserInfo(attributes);
            case APPLE: return new AppleUserInfo(attributes);
            default:
                throw new IllegalArgumentException( provider+"는 지원하지 않는 소셜 로그인입니다");
        }
    }
}
