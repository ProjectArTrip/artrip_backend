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
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleTokenVerifier implements SocialVerifier {

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.private-key}")
    private String privateKeyContent;

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

    private final RestTemplate restTemplate;

    @Override
    public Provider getProvider() {
        return Provider.APPLE;
    }

    @Override
    public SocialUserInfo verify(String idToken) {
        try {
            UrlJwkProvider jwkProvider = new UrlJwkProvider(new URL(APPLE_JWKS_URL));
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String kid = decodedJWT.getKeyId();

            Jwk jwk = jwkProvider.get(kid);
            Algorithm algorithm = Algorithm.ECDSA256((ECPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(APPLE_ISSUER)
                    .withAudience(clientId)
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

            String email = verified.getClaim("email").asString();
            String sub = verified.getSubject();
            // Apple은 최초 로그인 시에만 name 제공 → 이메일 앞부분을 임시 닉네임으로 사용
            String nickname = email != null ? email.split("@")[0] : sub;

            return SocialUserInfo.builder()
                    .email(email)
                    .nickname(nickname)
                    .providerId(sub)
                    .provider(getProvider())
                    .build();

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple token verification failed", e);
            throw new GeneralException(UserErrorCode._SOCIAL_VERIFICATION_FAILED);
        }
    }

    @Override
    public Map<String, String> exchangeCodeForTokens(String authorizationCode) {
        String clientSecret = generateClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(APPLE_TOKEN_URL, request, Map.class);

        Map<String, Object> body = response.getBody();
        Map<String, String> result = new HashMap<>();
        if (body != null) {
            result.put("id_token", (String) body.get("id_token"));
            result.put("refresh_token", (String) body.get("refresh_token"));
        }
        return result;
    }

    @Override
    public void unlink(String providerId, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;

        String clientSecret = generateClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", refreshToken);
        params.add("token_type_hint", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(APPLE_REVOKE_URL, request, String.class);
        } catch (Exception e) {
            log.error("Apple unlink 실패", e);
            throw e;
        }
    }

    private String generateClientSecret() {
        try {
            String pem = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            ECPrivateKey privateKey = (ECPrivateKey) kf.generatePrivate(spec);

            Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);
            Date now = new Date();
            Date exp = new Date(now.getTime() + 180L * 24 * 60 * 60 * 1000);

            return JWT.create()
                    .withKeyId(keyId)
                    .withIssuer(teamId)
                    .withIssuedAt(now)
                    .withExpiresAt(exp)
                    .withAudience(APPLE_ISSUER)
                    .withSubject(clientId)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("Apple client secret 생성 실패", e);
            throw new GeneralException(UserErrorCode._SOCIAL_VERIFICATION_FAILED);
        }
    }
}
