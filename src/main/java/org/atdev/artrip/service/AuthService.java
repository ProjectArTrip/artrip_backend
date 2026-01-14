package org.atdev.artrip.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.request.ReissueRequest;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.AuthErrorCode;
import org.atdev.artrip.jwt.JwtGenerator;
import org.atdev.artrip.jwt.JwtProvider;
import org.atdev.artrip.jwt.JwtToken;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.security.utill.CookieUtils;
import org.atdev.artrip.service.redis.RedisService;
import org.atdev.artrip.validator.social.SocialVerifier;
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


    @Transactional
    public String webReissueToken(ReissueRequest request, HttpServletResponse response) {

        User user = getUserFromRefreshToken(request);
        String newAccessToken = jwtGenerator.createAccessToken(user, user.getRole());

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(accessTokenExpirationMillis);

        response.addCookie(accessCookie);

        return newAccessToken;
    }

    @Transactional
    public SocialLoginResponse appReissueToken(ReissueRequest request) {

        User user = getUserFromRefreshToken(request);
        String newAccessToken = jwtGenerator.createAccessToken(user, user.getRole());

        return new SocialLoginResponse(
                newAccessToken,
                request.refreshToken(),
                false
        );
    }

    private User getUserFromRefreshToken(ReissueRequest request) {

        if (request == null || request.refreshToken() == null) {
            throw new GeneralException(UserErrorCode._INVALID_REFRESH_TOKEN);
        }

        String refreshToken = request.refreshToken();
        jwtProvider.validateRefreshToken(refreshToken);
        String userId = redisService.getValue(refreshToken);

        if (userId == null) {
            throw new GeneralException(UserErrorCode._INVALID_USER_REFRESH_TOKEN);
        }

        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }


    @Transactional
    public void webLogout(String refreshToken, HttpServletResponse response) {

        if(refreshToken == null) return;
        redisService.deleteKey(refreshToken);

        CookieUtils.expire("accessToken", response);
        CookieUtils.expire("refreshToken", response);
    }

    @Transactional
    public void appLogout(ReissueRequest request) {

        if (request == null || request.refreshToken() == null) return;

        String refreshToken = request.refreshToken();
        String accessToken = request.accessToken();

        if (accessToken != null) {
            long remainTime = jwtProvider.getExpiration(accessToken);

            if (remainTime>0)
                redisService.save("BLACKLIST:" + accessToken, "logout", remainTime);
        }
        redisService.deleteKey(refreshToken);
    }

    @Transactional
    public SocialLoginResponse loginWithSocial(String providerName, String idToken) {

        Provider provider = Provider.from(providerName);

        SocialVerifier verifier = socialVerifiers.stream()
                .filter(v -> v.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new GeneralException(AuthErrorCode._UNSUPPORTED_SOCIAL_PROVIDER));

        SocialUserInfo socialUser = verifier.verify(idToken);

        String email = socialUser.getEmail();
        if (email == null) {
            throw new GeneralException(AuthErrorCode._SOCIAL_EMAIL_NOT_PROVIDED);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        boolean isFirstLogin = userOptional.isEmpty();

        User user = userOptional.orElseGet(() -> userRepository.save(User.createUser(socialUser)));
        JwtToken jwt = jwtGenerator.generateToken(user, user.getRole());

        redisService.save(jwt.getRefreshToken(), String.valueOf(user.getUserId()), refreshTokenExpirationMillis);

        return new SocialLoginResponse(
                jwt.getAccessToken(),
                jwt.getRefreshToken(),
                isFirstLogin
        );
    }

    @Transactional
    public void completeOnboarding(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setOnboardingCompleted(!user.isOnboardingCompleted());
    }

}
