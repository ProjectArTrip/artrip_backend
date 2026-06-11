package org.atdev.artrip.utils;

public final class StringUtils {
    private StringUtils() {}

    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}
