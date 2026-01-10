package org.atdev.artrip.service.social;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Service
public class GoogleValidator implements SocialVerifier{

    @Value("${spring.security.oauth2.client.registration.google.aod-client-id}")
    private String googleAodClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String GOOGLE_ISSUER = "https://accounts.google.com";

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
                throw new GeneralException(UserError._SOCIAL_ID_TOKEN_INVALID);
            }

            String aud = audiences.get(0);
            String expectedAud;
            if (aud.equals(googleAodClientId)) {
                expectedAud = googleAodClientId;
            } else if (aud.equals(googleClientId)) {
                expectedAud = googleClientId;
            } else {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_AUDIENCE);
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
                throw new GeneralException(UserError._SOCIAL_TOKEN_EXPIRED);
            } catch (SignatureVerificationException e) {
                throw new GeneralException(UserError._SOCIAL_TOKEN_INVALID_SIGNATURE);
            } catch (Exception e) {
                throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
            }

            return SocialUserInfo.from(verified,getProvider());

        } catch (Exception e) {
            throw new GeneralException(UserError._SOCIAL_VERIFICATION_FAILED);
        }
    }
}
