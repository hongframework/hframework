package com.hframework.common.util;

import java.math.BigDecimal;

/**
 * 货币工具
 */
public class BigDecimalUtils {

    /**
     * 初始除法的精度 *
     */
    private static final int DEFAULT_SCALE = 0;
    /**
     * 默认为四舍五入方法*
     */
    private static int DEFAULT_ROUND_PATTERN = BigDecimal.ROUND_HALF_UP;

    /**
     * 转换为货币。 采用了四舍五入的方法.
     *
     * @param currency 待转换的数字。
     * @param p        小数点后保留的位数.
     * @return 转换后的数字.
     */
    public static double round(double currency, int p) {
        BigDecimal decimal = BigDecimal.valueOf(currency);
        return decimal.divide(new BigDecimal(1), p, DEFAULT_ROUND_PATTERN).doubleValue();
    }

    /**
     * 浮点类型的四舍五入.
     *
     * @param currency 待转换的数字。
     * @param p        保留的位数。
     * @return 转换后的结果.
     */
    public static float round(float currency, int p) {
        BigDecimal decimal = BigDecimal.valueOf(currency);
        return decimal.divide(new BigDecimal(1), p, DEFAULT_ROUND_PATTERN).floatValue();
    }

    /**
     * 提供了两个double类型的相加。
     *
     * @param v 值一
     * @return 加后的结果.
     */
    public static double plus(double... v) {
        BigDecimal b = new BigDecimal(0);
        for (int i = 0; i < v.length; i++) {
            b = b.add(BigDecimal.valueOf(v[i]));
        }
        return b.doubleValue();
    }

    /**
     * 提供两个数字的减法操作. v1-v2
     *
     * @param v1 值1
     * @param v2 值2
     * @return
     */
    public static double minus(double v1, double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供两个数字的除法。(v1/v2)
     *
     * @param v1 值1
     * @param v2 值2
     * @return 除法后的结果.
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEFAULT_SCALE);
    }

    /**
     * 提供两个数字的除法。(v1/v2)
     *
     * @param v1 值1
     * @param v2 值2
     * @param p  精度(小数点后的位数)
     * @return 除法后的结果.
     */
    public static double div(double v1, double v2, int p) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, p, DEFAULT_ROUND_PATTERN).doubleValue();
    }

    /**
     * 提供两个数字的乘法(v1与v2相乘)
     *
     * @param v1 值1
     * @param v2 值2
     * @return 乘法后的结果.
     */
    public static double mult(double v1, double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.multiply(b2).doubleValue();
    }
}
