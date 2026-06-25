package com.hotel.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    /**
     * 计算两个日期之间的天数差（忽略时间部分）
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算入住天数（实际离店时间或当前时间 - 入住日期）
     */
    public static long calcStayDays(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkOut == null) {
            checkOut = LocalDateTime.now();
        }
        // 计算两个日期之间的天数（不足一天按一天算）
        LocalDate startDate = checkIn.toLocalDate();
        LocalDate endDate = checkOut.toLocalDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        // 如果离店时间 > 入住日期 + 天数，则说明超过了整天，再加1天
        LocalDateTime endOfStartDay = startDate.atTime(23, 59, 59);
        if (checkOut.isAfter(endOfStartDay.plusDays(days))) {
            days++;
        }
        return days == 0 ? 1 : days; // 最少1天
    }

    /**
     * 获取当前时间字符串（用于日志）
     */
    public static String nowStr() {
        return LocalDateTime.now().toString();
    }
}