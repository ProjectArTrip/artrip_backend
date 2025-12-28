package org.atdev.artrip.domain.auth.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.Enum.Provider;
import org.atdev.artrip.domain.Enum.Role;
import org.atdev.artrip.domain.auth.data.SocialAccounts;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.jwt.JwtGenerator;
import org.atdev.artrip.domain.auth.jwt.JwtProvider;
import org.atdev.artrip.domain.auth.jwt.JwtToken;
import org.atdev.artrip.domain.auth.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.auth.web.dto.request.ReissueRequest;
import org.atdev.artrip.domain.auth.web.dto.response.SocialLoginResponse;
import org.atdev.artrip.domain.auth.web.dto.response.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.native-client-id}")
    private String kakaoNativeClientId;

    @Value("${spring.security.oauth2.client.registration.google.aod-client-id}")
    private String googleAodClientId;


    @Transactional
    public String webReissueToken(ReissueRequest request, HttpServletResponse response) {

        User user = getUserFromRefreshToken(request);

        String newAccessToken = jwtGenerator.createAccessToken(user.getUserId().toString(), user.getRole().name());

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);

        response.addCookie(accessCookie);

        return newAccessToken;
    }

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

    private User getUserFromRefreshToken(ReissueRequest request) {// SRP책임 분리 - 유저와 토큰 검증 + 레디스 적재

        if (request == null || request.getRefreshToken() == null) {
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN);
        }

        String refreshToken = request.getRefreshToken();

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(refreshToken);

        if (userId == null) {
            throw new GeneralException(UserError._INVALID_USER_REFRESH_TOKEN);
        }

        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));
    }


    @Transactional
    public void webLogout(String refreshToken, HttpServletResponse response) {

        if(refreshToken == null) return;

        refreshTokenRedisRepository.delete(refreshToken);

        expireCookie("accessToken", response);
        expireCookie("refreshToken", response);
    }

    @Transactional
    public void appLogout(ReissueRequest request) {

        if (request == null || request.getRefreshToken() == null) return;

        String refreshToken = request.getRefreshToken();

        refreshTokenRedisRepository.delete(refreshToken);
    }

    private void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }

    @Transactional
    public SocialLoginResponse loginWithSocial(String provider, String idToken) {

        SocialUserInfo socialUser = switch (provider.toUpperCase()) {
            case "KAKAO" -> verifyKakao(idToken);
            case "GOOGLE" -> verifyGoogle(idToken);
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        };

        log.info("social 정보: {}",socialUser);
        log.info("social email: {}",socialUser.getEmail());

        String email = socialUser.getEmail() != null
                ? socialUser.getEmail()
                : provider.toLowerCase() + socialUser.getProviderId() + "@example.com";

        User user = userRepository.findByEmail(email).
                orElseGet(()->createNewUser(socialUser, email, provider));


        boolean isFirstLogin= !user.isOnboardingCompleted();

        JwtToken jwt = jwtGenerator.generateToken(user, user.getRole());

        refreshTokenRedisRepository.save(
                jwt.getRefreshToken(),
                String.valueOf(user.getUserId()),
                1000L * 60 * 60 * 24 * 7
        );

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

    private User createNewUser(SocialUserInfo info, String email, String providerStr) {

        Provider provider = switch (providerStr.toUpperCase()) {
            case "KAKAO" -> Provider.KAKAO;
            case "GOOGLE" -> Provider.GOOGLE;
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + providerStr);
        };

        User user = User.builder()
                .email(email)
                .name(info.getNickname())
                .role(Role.USER)
                .onboardingCompleted(false)
                .build();

        SocialAccounts social = SocialAccounts.builder()
                .user(user)
                .provider(provider)
                .providerId(info.getProviderId())
                .build();

        user.getSocialAccounts().add(social);

        User savedUser = userRepository.save(user);

        return savedUser;
    }


    private SocialUserInfo verifyKakao(String idToken) {
        try {
            log.info("Kakao ID Token 검증 시작");
            log.info("Kakao REST API Client ID: {}", kakaoClientId);
            log.info("kakao Native Client ID: {}", kakaoNativeClientId);
            
            String jwksUrl = "https://kauth.kakao.com/.well-known/jwks.json";
            UrlJwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();
            List<String> audiences = decodedJWT.getAudience();
            log.info("Token Kid: {}", kid);
            log.info("Token Issuer: {}", decodedJWT.getIssuer());
            log.info("Token Audience: {}", decodedJWT.getAudience());

            if (audiences == null || audiences.isEmpty()) {
                throw new GeneralException(UserError._SOCIAL_ID_TOKEN_INVALID);
            }

            String aud = audiences.get(0);
            String expectedAud;
            if (aud.equals(kakaoNativeClientId)) {
                expectedAud = kakaoNativeClientId;
                log.info("➡ Android Native SDK 토큰으로 판단");
            } else if (aud.equals(kakaoClientId)) {
                expectedAud = kakaoClientId;
                log.info("➡ 서버용 REST API 토큰으로 판단");
            } else {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_AUDIENCE);
            }

            Jwk jwk = provider.get(kid);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://kauth.kakao.com")
                    .withAudience(expectedAud)
                    .build();

            DecodedJWT verified;

            try {
                verified = verifier.verify(idToken);
            } catch (TokenExpiredException e) {
                throw new GeneralException(UserError._SOCIAL_TOKEN_EXPIRED);
            } catch (SignatureVerificationException e) {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_SIGNATURE);
            } catch (Exception e) {
                throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
            }


            String email = verified.getClaim("email").asString();
            String nickname = verified.getClaim("nickname").asString();
            String sub = verified.getSubject();
            log.info("Kakao 토큰 검증 성공 - email:{}, nickname:{}, sub:{}",email,nickname,sub);
            return new SocialUserInfo(email, nickname, sub);

        } catch (Exception e) {
            log.error("Kakao ID Token 검증 실패: {}", e.getMessage(), e);
            throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
        }
    }


    private SocialUserInfo verifyGoogle(String idToken) {
        try {
            log.info("Google ID Token 검증 시작");
            log.info("Google Client ID: {}", googleClientId);
            
            String jwksUrl = "https://www.googleapis.com/oauth2/v3/certs";
            UrlJwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();
            List<String> audiences = decodedJWT.getAudience();

            if (audiences == null || audiences.isEmpty()) {
                throw new GeneralException(UserError._SOCIAL_ID_TOKEN_INVALID);
            }
            log.info("Token Kid: {}", kid);
            log.info("Token Issuer: {}", decodedJWT.getIssuer());
            log.info("Token Audience: {}", decodedJWT.getAudience());

            String aud = audiences.get(0);
            String expectedAud;
            if (aud.equals(googleAodClientId)) {
                expectedAud = googleAodClientId;
                log.info("➡ google aod SDK 토큰으로 판단");
            } else if (aud.equals(googleClientId)) {
                expectedAud = googleClientId;
                log.info("➡ 서버용 REST API 토큰으로 판단");
            } else {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_AUDIENCE);
            }

            Jwk jwk = provider.get(kid);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://accounts.google.com")
                    .withAudience(expectedAud)
                    .build();

            DecodedJWT verified;
            try {
                verified = verifier.verify(idToken);
            } catch (TokenExpiredException e) {
                throw new GeneralException(UserError._SOCIAL_TOKEN_EXPIRED);
            } catch (SignatureVerificationException e) {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_SIGNATURE);
            } catch (Exception e) {
                throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
            }

            String email = verified.getClaim("email").asString();
            String name = verified.getClaim("name").asString();
            String sub = verified.getSubject();
            
            log.info("Google 토큰 검증 성공 - email: {}, name: {}", email, name);

            return new SocialUserInfo(email, name, sub);

        } catch (Exception e) {
            log.error("Google ID Token 검증 실패: {}", e.getMessage(), e);
            throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
        }
    }


}
