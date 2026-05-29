package org.atdev.artrip.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.oauth.OAuth2UserInfo;
import org.atdev.artrip.global.apipayload.code.error.AuthErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.jwt.JwtGenerator;
import org.atdev.artrip.jwt.JwtToken;
import org.atdev.artrip.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.security.utill.CookieUtils;
import org.atdev.artrip.security.utill.TokenKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final CookieUtils cookieUtils;

    @Value("${app.front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        Provider provider = Provider.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        User user = userRepository.findBySocialAccountsProviderAndProviderId(provider, userInfo.getId())
                .orElseThrow(() -> new GeneralException(AuthErrorCode._WEB_LOGIN_USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(AuthErrorCode._WEB_LOGIN_FORBIDDEN);
        }

        JwtToken token = jwtGenerator.generateToken(user, user.getRole());

        refreshTokenRedisRepository.save(
                TokenKeys.refreshKey(token.getRefreshToken()),
                String.valueOf(user.getUserId()),
                REFRESH_TTL.toMillis()
        );

        cookieUtils.writeAccessToken(response, token.getAccessToken(), ACCESS_TTL);
        cookieUtils.writeRefreshToken(response, token.getRefreshToken(), REFRESH_TTL);

        response.sendRedirect(frontUrl + "/admin/exhibits");
    }

}