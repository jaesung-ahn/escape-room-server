package com.wiiee.server.api.domain.util;

import com.wiiee.server.common.domain.gathering.AgeGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LocalDateTimeUtil {

    private static final String BASIC_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    public static String getLocalDateTimeNowString(String pattern) {
        return getLocalDateTimeNow().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getFormattingLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(BASIC_PATTERN));
    }

    public static String getDateFormat(LocalDateTime createdAt) {
        LocalDateTime now = getLocalDateTimeNow();
        String dateFormat = "";
        long epoch = now.toInstant(ZoneOffset.UTC).getEpochSecond() - createdAt.toInstant(ZoneOffset.UTC).getEpochSecond();

        if (createdAt.isAfter(now.minusMinutes(1))) {
            dateFormat += epoch + "초전";
        } else if (createdAt.isAfter(now.minusHours(1))) {      // 1시간전에 등록됨 -> n분전.
            dateFormat += epoch/60 + "분전";
        } else if (createdAt.isAfter(now.minusDays(1))) {       //하루전에 등록됨. -> n시간전
            dateFormat += epoch/(60*60) + "시간전";
        } else if (createdAt.isAfter(now.minusMonths(1))) {     //한달 이전 등록됨 -> n일전
            dateFormat += epoch/(60*60*24) + "일전";
        } else {                                                //그외 -> n개월전
            dateFormat += epoch/(60*60*24*30) + "개월전";
        }
        return dateFormat;
    }

    // 나이 연령대 구하기
    public static String getAgeGroup(LocalDate ageDate) {
        Calendar current = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay = current.get(Calendar.DAY_OF_MONTH);
        // 생일 지나거나 안 지난 여부 생략
        int age = currentYear - (ageDate.getYear() - 1);

        if (age < 10) {
            return AgeGroup.valueOf(0).getName();
        }
        else {
            int resultAge = age / 10;
            if (resultAge >= 6) {
                resultAge = 6;
            }
            return AgeGroup.valueOf(resultAge).getName();
        }
    }

    // 요일 조회
    public static String getDayOfTheWeek(LocalDateTime localDate) {
        Date currentDate = java.sql.Timestamp.valueOf(localDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN);
    }
}