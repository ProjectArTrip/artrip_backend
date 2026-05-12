package org.atdev.artrip.validator.social;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier implements SocialVerifier{

    @Value("${spring.security.oauth2.client.registration.google.project-id}")
    private String projectId;

    private String getIssuer() {
        return "https://securetoken.google.com/" + projectId;
    }

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com";
    private final RestTemplate restTemplate;

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public SocialUserInfo verify(String idToken) {

        try {
            UrlJwkProvider provider = new UrlJwkProvider(new URL(GOOGLE_JWKS_URL));

            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();
            List<String> audiences = decodedJWT.getAudience();

            if (audiences == null || audiences.isEmpty()) {
                throw new GeneralException(UserErrorCode._SOCIAL_ID_TOKEN_INVALID);
            }

            String aud = audiences.get(0);

            if (!aud.equals(projectId)) {
                throw new GeneralException(UserErrorCode._SOCIAL_TOKEN_INVALID_AUDIENCE);
            }

            Jwk jwk = provider.get(kid);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(getIssuer())
                    .withAudience(projectId)
                    .build();

            DecodedJWT verified;
            try {
                verified = verifier.verify(idToken);
            } catch (TokenExpiredException e) {
                throw new GeneralException(UserErrorCode._SOCIAL_TOKEN_EXPIRED);
            } catch (SignatureVerificationException e) {
                throw new GeneralException(UserErrorCode._SOCIAL_TOKEN_INVALID_SIGNATURE);
            } catch (Exception e) {
                throw new GeneralException(UserErrorCode._SOCIAL_VERIFICATION_FAILED);
            }

            String nickname = verified.getClaim("name").asString();

            return SocialUserInfo.of(verified, nickname, getProvider());

        } catch (Exception e) {
            throw new GeneralException(UserErrorCode._SOCIAL_VERIFICATION_FAILED);
        }
    }

    @Override
    public void unlink(String providerId, String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        String url = "https://oauth2.googleapis.com/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.error("Google unlink 실패", e);
            throw e;
        }
    }
}
