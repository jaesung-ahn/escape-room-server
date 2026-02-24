package com.wiiee.server.common.util;

public final class LogMaskingUtil {

    private LogMaskingUtil() {
    }

    public static String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
