package org.atdev.artrip.constants;

import org.atdev.artrip.global.apipayload.code.status.AuthErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import java.util.Arrays;

public enum Provider {
    GOOGLE,
    KAKAO,
    APPLE;


    public static Provider from(String providerName){

        if(providerName==null){
            throw new GeneralException(AuthErrorCode._SOCIAL_EMAIL_NOT_PROVIDED);
        }

        return Arrays.stream(Provider.values())
                .filter(p -> p.name().equalsIgnoreCase(providerName))
                .findFirst()
                .orElseThrow(() -> new GeneralException(AuthErrorCode._UNSUPPORTED_SOCIAL_PROVIDER));
    }
}
