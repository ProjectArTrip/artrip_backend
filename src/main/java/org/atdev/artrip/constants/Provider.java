package org.atdev.artrip.constants;

import org.atdev.artrip.global.apipayload.code.status.AuthErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

public enum Provider {
    GOOGLE,
    KAKAO,
    APPLE;


    public static Provider from(String providerName){

        if(providerName==null){
            throw new GeneralException(AuthErrorCode._SOCIAL_EMAIL_NOT_PROVIDED);
        }
        try {
            return Provider.valueOf(providerName.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new GeneralException(AuthErrorCode._UNSUPPORTED_SOCIAL_PROVIDER);
        }
    }
}
