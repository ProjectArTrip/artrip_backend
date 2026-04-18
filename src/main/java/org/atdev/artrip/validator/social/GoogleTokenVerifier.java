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
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier implements SocialVerifier{

    @Value("${spring.security.oauth2.client.registration.google.aod-client-id}")
    private String googleAodClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String GOOGLE_ISSUER = "https://accounts.google.com";
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
            String expectedAud;
            if (aud.equals(googleAodClientId)) {
                expectedAud = googleAodClientId;
            } else if (aud.equals(googleClientId)) {
                expectedAud = googleClientId;
            } else {
                throw new GeneralException(UserErrorCode._SOCIAL_TOKEN_INVALID_AUDIENCE);
            }

            Jwk jwk = provider.get(kid);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(GOOGLE_ISSUER)
                    .withAudience(expectedAud)
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
    public Map<String, String> exchangeCodeForTokens(String authorizationCode) {
        log.info("redirect_uri: '{}'", redirectUri);
        log.info("authorizationCode: '{}'", authorizationCode);

        Map<String, Object> tokenResponse = exchangeAuthorizationCode(authorizationCode);

        Map<String, String> result = new java.util.HashMap<>();
        if (tokenResponse != null) {
            result.put("id_token", (String) tokenResponse.get("id_token"));
            result.put("refresh_token", (String) tokenResponse.get("refresh_token"));
        }
        return result;
    }


    private Map<String, Object> exchangeAuthorizationCode(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", params, Map.class
        );

        return response.getBody();
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
