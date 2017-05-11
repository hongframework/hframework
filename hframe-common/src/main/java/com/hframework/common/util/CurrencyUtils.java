package com.hframework.common.util;

/**
 * 货币工具
 *
 */
public class CurrencyUtils {

    private static final String[] numbers = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] units = {"圆", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿",
            "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿",
            "拾", "佰", "仟", "万", "拾", "佰", "仟"};

    /**
     * 人民币数字转换为大写
     *
     * @param money 人民币数字
     * @return 转换后的大写
     */
    private static String convertRMB(String money) {
        int len = money.length();
        if (len > 15) {
            return "数值过大!";
        }
        // 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
        StringBuffer rmbStr = new StringBuffer();
        boolean lastzero = false;
        // 亿、万进位前有数值标记
        boolean hasmoneyue = false;

        for (int i = len - 1; i >= 0; i--) {
            if (money.charAt(len - i - 1) == ' ') {
                continue;
            }

            int n = money.charAt(len - i - 1) - '0';

            if (n < 0 || n > 9) {
                return "输入含非数字字符!";
            }

            if (n != 0) {
                // 若干零后若跟非零值，只显示一个零
                if (lastzero) {
                    rmbStr.append(numbers[0]);
                }
                // 十进位处于第一位不发壹音
                if (!(n == 1 && (i % 4) == 1 && i == len - 1)) {
                    rmbStr.append(numbers[n]);
                }
                // 非零值后加进位，个位为空
                rmbStr.append(units[i]);
                // 置万进位前有值标记
                hasmoneyue = true;
            } else {
                // 亿万之间必须有非零值方显示万
                if ((i % 8) == 0 || ((i % 8) == 4 && hasmoneyue)) {
                    // “亿”或“万”
                    rmbStr.append(units[i]);
                }
            }

            // 万进位前有值标记逢亿复位
            if (i % 8 == 0) {
                hasmoneyue = false;
            }

            lastzero = (n == 0) && (i % 4 != 0);
        }

        // 输入空字符或"0"，返回"零"
        if (rmbStr.length() == 0) {
            rmbStr.append(numbers[0]);
        }

        return rmbStr.toString();
    }

    /**
     * 人民币数字转换为大写
     *
     * @param money 人民币数字
     * @return 转换后的大写
     */
    public static String convertRMB(double money) {
        String SignStr = "";
        String TailStr = "";

        if (money < 0) {
            money = -money;
            SignStr = "负";
        }

        if (money > 99999999999999.999 || money < -99999999999999.999) {
            return "数值位数过大!";
        }

        // 四舍五入到分
        long temp = Math.round(money * 100);
        long integer = temp / 100;
        long fraction = temp % 100;
        int jiao = (int) fraction / 10;
        int fen = (int) fraction % 10;
        if (jiao != 0 || fen != 0) {
            TailStr = numbers[jiao];
            if (jiao != 0) {
                TailStr += "角";
            }
            // 零元后不写零几分
            if (integer == 0 && jiao == 0) {
                TailStr = "";
            }
            if (fen != 0) {
                TailStr += numbers[fen] + "分";
            }
        }
        return SignStr + convertRMB(String.valueOf(integer)) + TailStr;
    }

    public static void main(String[] args) {
        double money = 1000024.99;
        System.out.println(convertRMB(money));

    }
}
