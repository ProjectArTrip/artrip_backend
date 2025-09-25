package org.atdev.artrip.auth.Oauth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.auth.jwt.JwtGenerator;
import org.atdev.artrip.auth.jwt.JwtToken;
import org.atdev.artrip.auth.jwt.entity.RefreshToken;
import org.atdev.artrip.auth.jwt.repository.RefreshTokenRepository;
import org.atdev.artrip.domain.Enum.Provider;
import org.atdev.artrip.domain.User;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtGenerator jwtGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("들어옴.");

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        String registrationId = authToken.getAuthorizedClientRegistrationId(); // "kakao", "google", "apple"
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        String providerId = userInfo.getId();     // provider별 구현체에서 가져오는 고유 ID
        String email = userInfo.getEmail();       // provider별 구현체에서 가져오는 이메일
        String name = userInfo.getName();         // provider별 구현체에서 가져오는 이름


        User user = userRepository.findBySocialAccountsProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        JwtToken jwtToken = jwtGenerator.generateToken(user, user.getRole());

        // RefreshToken 저장 (username = providerId 로 임시 관리)
        refreshTokenRepository.findByUsername(providerId)
                .ifPresentOrElse(
                        existing -> {
                            RefreshToken updated = existing.toBuilder()
                                    .refreshToken(jwtToken.getRefreshToken())
                                    .build();
                            refreshTokenRepository.save(updated);
                        },
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .username(providerId) // 임시 추후 email+redis로 변경
                                        .refreshToken(jwtToken.getRefreshToken())
                                        .build())
                );

//        String email = (String) oAuth2User.getAttributes().get("email");
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));
//
//        JwtToken jwtToken = jwtGenerator.generateToken(user, user.getRole());
//
//        // RefreshToken 저장
//        refreshTokenRepository.findByUsername(email)
//                .ifPresentOrElse(
//                        existing -> {
//                            RefreshToken updated = existing.toBuilder()
//                                    .refreshToken(jwtToken.getRefreshToken())
//                                    .build();
//                            refreshTokenRepository.save(updated); //추후 디비가 아닌 레디스로 관리하자
//                        },
//                        () -> refreshTokenRepository.save(
//                                RefreshToken.builder()
//                                        .username(email)
//                                        .refreshToken(jwtToken.getRefreshToken())
//                                        .build())
//                );

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTPS 환경일 경우 true, 개발 중이면 false
                .path("/")
                .sameSite("Lax") // 프론트 분리 환경에서는 None (CORS 허용)
                .maxAge(7 * 24 * 60 * 60)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", jwtToken.getAccessToken())
                .httpOnly(true)
                .secure(false) // HTTPS 환경일 경우 true, 개발 중이면 false
                .path("/")
                .sameSite("Lax") // 프론트 분리 환경에서는 None (CORS 허용)
                .maxAge(900)
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", accessCookie.toString());

        log.info("로그인성공 실행됨");

//        response.sendRedirect("/");
//        getRedirectStrategy().sendRedirect(request, response, redirectUri);


    }
}