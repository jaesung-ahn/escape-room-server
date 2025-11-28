package com.wiiee.server.admin.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
//    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static LocalDateTime parseDate(String dateStr) {
//        Date date = formatter.parse(dateStr);

        return LocalDateTime.parse(dateStr);
    }

    public static String formatDate(LocalDateTime date) {
        String dateStr = null;

        if (date != null) {
            dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return dateStr;
    }

    public static String formatDateTime(LocalDateTime date) {
        String dateStr = null;

        if (date != null) {
            dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        }
        return dateStr;
    }

}
