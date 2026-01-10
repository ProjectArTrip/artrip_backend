package org.atdev.artrip.controller.dto.response;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.constants.Provider;

@AllArgsConstructor
@Data
@Builder
public class SocialUserInfo {
    private String email;
    private String nickname;
    private String providerId;
    private Provider provider;

    public static SocialUserInfo from(DecodedJWT verified, Provider provider) {
        return SocialUserInfo.builder()
                .email(verified.getClaim("email").asString())
                .nickname(extractNickname(verified, provider))
                .providerId(verified.getSubject())
                .provider(provider)
                .build();
    }

    private static String extractNickname(DecodedJWT verified, Provider provider) {
        if (provider == Provider.GOOGLE) {
            return verified.getClaim("name").asString();
        }
        if (provider == Provider.KAKAO) {
            return verified.getClaim("nickname").asString();
        }
        return null;
    }
}
