package com.hotel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {

    /**
     * 格式化金额（保留两位小数），线程安全
     */
    public static String format(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * 安全加法
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.add(b);
    }

    /**
     * 安全减法
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.subtract(b);
    }

    /**
     * 计算总金额 = 单价 * 数量
     */
    public static BigDecimal multiply(BigDecimal price, Integer quantity) {
        if (price == null) price = BigDecimal.ZERO;
        if (quantity == null) quantity = 0;
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}