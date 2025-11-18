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
import org.atdev.artrip.domain.auth.web.dto.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtGenerator jwtGenerator;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Transactional
    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new GeneralException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(refreshToken);

        if (userId == null) {
            throw new GeneralException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

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
    public JwtToken loginWithSocial(String provider, String idToken) {

        SocialUserInfo socialUser = switch (provider.toUpperCase()) {
            case "KAKAO" -> verifyKakao(idToken);
            case "GOOGLE" -> verifyGoogle(idToken);
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        };

        User user = userRepository.findByEmail(socialUser.getEmail())
                .orElseGet(() -> createNewUser(socialUser));

        JwtToken jwt = jwtGenerator.generateToken(user, user.getRole());

        return jwt;
    }

    private User createNewUser(SocialUserInfo info) {


        User user = User.builder()
                .email(info.getEmail())
                .name(info.getNickname())
                .role(Role.USER)
                .build();

        SocialAccounts social = SocialAccounts.builder()
                .user(user)
                .provider(Provider.KAKAO)
                .providerId(info.getProviderId())
                .build();

        user.getSocialAccounts().add(social);

        return userRepository.save(user);

    }


    private SocialUserInfo verifyKakao(String idToken) {
        try {
            String jwksUrl = "https://kauth.kakao.com/.well-known/jwks.json";
            UrlJwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();

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

            return new SocialUserInfo(email, nickname, sub);


        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._SOCIAL_VERIFICATION_FAILED);
        }
    }


    private SocialUserInfo verifyGoogle(String idToken) {
        try {
            String jwksUrl = "https://www.googleapis.com/oauth2/v3/certs";
            UrlJwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();

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

            return new SocialUserInfo(email, name, sub);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._SOCIAL_VERIFICATION_FAILED);
        }
    }

}
