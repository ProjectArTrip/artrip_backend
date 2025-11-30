package org.atdev.artrip.domain.auth.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.Enum.Provider;
import org.atdev.artrip.domain.Enum.Role;
import org.atdev.artrip.domain.SocialAccounts;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.jwt.JwtGenerator;
import org.atdev.artrip.domain.auth.jwt.JwtProvider;
import org.atdev.artrip.domain.auth.jwt.JwtToken;
import org.atdev.artrip.domain.auth.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.auth.web.dto.SocialLoginResponse;
import org.atdev.artrip.domain.auth.web.dto.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

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

    @Transactional
    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN);
        }

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(refreshToken);

        if (userId == null) {
            throw new GeneralException(UserError._INVALID_USER_REFRESH_TOKEN);
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

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
    public SocialLoginResponse reissueAppToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null) {
            throw new GeneralException(UserError._INVALID_REFRESH_TOKEN);
        }

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(refreshToken);

        if (userId == null) {
            throw new GeneralException(UserError._INVALID_USER_REFRESH_TOKEN);
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        String newAccessToken = jwtGenerator.createAccessToken(user.getUserId().toString(), user.getRole().name());

        return new SocialLoginResponse(
                newAccessToken,
                refreshToken,
                false
        );
    }
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {

        if(refreshToken != null) {
            refreshTokenRedisRepository.delete(refreshToken);
        }

        expireCookie("accessToken", response);
        expireCookie("refreshToken", response);
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
                : "kakao_" + socialUser.getProviderId() + "@example.com";

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        boolean isFirstLogin;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            isFirstLogin = false; // 기존 사용자
        } else {
            user = createNewUser(socialUser, email);
            isFirstLogin = true;  // 신규 생성
        }
        log.info("user:{}",user);

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

    private User createNewUser(SocialUserInfo info, String email) {


        User user = User.builder()
                .email(email)
                .name(info.getNickname())
                .role(Role.USER)
                .build();

        SocialAccounts social = SocialAccounts.builder()
                .user(user)
                .provider(Provider.KAKAO)
                .providerId(info.getProviderId())
                .build();


        user.getSocialAccounts().add(social);

        User savedUser = userRepository.save(user);

        return savedUser;
    }


    private SocialUserInfo verifyKakao(String idToken) {
        try {
            log.info("Kakao ID Token 검증 시작");
            log.info("Kakao Client ID: {}", kakaoClientId);
            
            String jwksUrl = "https://kauth.kakao.com/.well-known/jwks.json";
            UrlJwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();
            log.info("Token Kid: {}", kid);
            log.info("Token Issuer: {}", decodedJWT.getIssuer());
            log.info("Token Audience: {}", decodedJWT.getAudience());

            Jwk jwk = provider.get(kid);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://kauth.kakao.com")
                    .withAudience(kakaoClientId)
                    .build();

            DecodedJWT verified = verifier.verify(idToken);

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
            log.info("Token Kid: {}", kid);
            log.info("Token Issuer: {}", decodedJWT.getIssuer());
            log.info("Token Audience: {}", decodedJWT.getAudience());

            Jwk jwk = provider.get(kid);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://accounts.google.com")
                    .withAudience(googleClientId)
                    .build();

            DecodedJWT verified = verifier.verify(idToken);

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
