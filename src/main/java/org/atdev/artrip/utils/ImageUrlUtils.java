package org.atdev.artrip.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUrlUtils {
    private static final String DEFAULT_POSTER_URL = "";

    public static String posterUrlDefault(String posterUrl) {
        if (posterUrl == null || posterUrl.isBlank()) {
            return DEFAULT_POSTER_URL;
        }
        return posterUrl;
    }
}
