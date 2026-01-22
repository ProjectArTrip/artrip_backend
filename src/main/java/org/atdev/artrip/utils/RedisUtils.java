package org.atdev.artrip.utils;

public class RedisUtils {

    private static final String RECENT_VIEW_PREFIX = "user:history:";

    public static String getRecentViewKey(Long userId) {
        return RECENT_VIEW_PREFIX + userId;
    }
}