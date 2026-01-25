package org.atdev.artrip.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NicknameUtils {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]+$");

    public static String getValidatedNickname(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        String trimmedNick = nickName.trim();

        if (trimmedNick.contains(" ") || !NICKNAME_PATTERN.matcher(trimmedNick).matches()) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        return trimmedNick;
    }
}
