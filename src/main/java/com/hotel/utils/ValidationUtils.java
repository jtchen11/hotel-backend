package com.hotel.utils;

public class ValidationUtils {

    public static boolean isPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    public static boolean isIdCard(String idCard) {
        return idCard != null && idCard.matches("^\\d{17}[\\dXx]$");
    }

    public static boolean isPositive(Integer num) {
        return num != null && num > 0;
    }
}