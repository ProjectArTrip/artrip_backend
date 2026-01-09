package org.atdev.artrip.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.SocialAccounts;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.AuthError;
import org.atdev.artrip.jwt.JwtGenerator;
import org.atdev.artrip.jwt.JwtProvider;
import org.atdev.artrip.jwt.JwtToken;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.controller.dto.request.ReissueRequest;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.social.SocialVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final List<SocialVerifier> socialVerifiers;
    private final RedisService redisService;

    @Value("${spring.jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;

    @Value("${spring.jwt.access-token-expiration-millis}")
    private int accessTokenExpirationMillis;


    // 웹 관리자 전용
    @Transactional
    public String webReissueToken(ReissueRequest request, HttpServletResponse response) {

        User user = getUserFromRefreshToken(request);
        String newAccessToken = jwtGenerator.createAccessToken(user.getUserId().toString(), user.getRole().name());

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(accessTokenExpirationMillis);

        response.addCookie(accessCookie);

        return newAccessToken;
    }

    // 앱 사용자 전용
    @Transactional
    public SocialLoginResponse appReissueToken(ReissueRequest request) {

        User user = getUserFromRefreshToken(request);
        String newAccessToken = jwtGenerator.createAccessToken(user.getUserId().toString(), user.getRole().name());

        return new SocialLoginResponse(
                newAccessToken,
                request.getRefreshToken(),
                false
        );
    }

    private User getUserFromRefreshToken(ReissueRequest request) {

        if (request == null || request.getRefreshToken() == null) {
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN);
        }

        String refreshToken = request.getRefreshToken();
        jwtProvider.validateRefreshToken(refreshToken);
        String userId = redisService.getValue(refreshToken);

        if (userId == null) {
            throw new GeneralException(UserError._INVALID_USER_REFRESH_TOKEN);
        }

        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));
    }


    @Transactional
    public void webLogout(String refreshToken, HttpServletResponse response) {

        if(refreshToken == null) return;
        redisService.deleteKey(refreshToken);

        expireCookie("accessToken", response);
        expireCookie("refreshToken", response);
    }

    @Transactional
    public void appLogout(ReissueRequest request) {

        if (request == null || request.getRefreshToken() == null) return;

        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();

        if (accessToken != null) {
            long remainTime = jwtProvider.getExpiration(accessToken);

            if (remainTime>0)
                redisService.saveBlacklist(accessToken, remainTime);
        }
        redisService.deleteKey(refreshToken);
    }

    private void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Transactional
    public SocialLoginResponse loginWithSocial(String providerStr, String idToken) {

        Provider provider = parseProvider(providerStr);

        SocialVerifier verifier = socialVerifiers.stream()
                .filter(v -> v.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new GeneralException(AuthError._UNSUPPORTED_SOCIAL_PROVIDER));

        SocialUserInfo socialUser = verifier.verify(idToken);

        String email = socialUser.getEmail();
        if (email == null) {
            throw new GeneralException(AuthError._SOCIAL_EMAIL_NOT_PROVIDED);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        boolean isFirstLogin = userOptional.isEmpty();

        User user = userOptional.orElseGet(() -> createNewUser(socialUser));
        JwtToken jwt = jwtGenerator.generateToken(user, user.getRole());

        redisService.save(jwt.getRefreshToken(), String.valueOf(user.getUserId()), refreshTokenExpirationMillis);

        return new SocialLoginResponse(
                jwt.getAccessToken(),
                jwt.getRefreshToken(),
                isFirstLogin
        );
    }

    private Provider parseProvider(String providerStr) {
        try {
            return Provider.valueOf(providerStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new GeneralException(AuthError._UNSUPPORTED_SOCIAL_PROVIDER);
        }
    }

    @Transactional// 테스트용 로직 -> 프론트 요청
    public void completeOnboarding(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setOnboardingCompleted(!user.isOnboardingCompleted());
    }

    private User createNewUser(SocialUserInfo info) {

        User user = User.builder()
                .email(info.getEmail())
                .name(info.getNickname())
                .role(Role.USER)
                .onboardingCompleted(false)
                .build();

        SocialAccounts social = SocialAccounts.builder()
                .user(user)
                .provider(info.getProvider())
                .providerId(info.getProviderId())
                .build();

        user.getSocialAccounts().add(social);
        return userRepository.save(user);
    }
}
