package com.hframework.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 日期
     */
    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 时间
     */
    public final static String TIME = "HH:mm:ss";

    /**
     * 日期与时间(24小时)
     */
    public static final String YYYY_MM_DD_24H_MM_SS = YYYY_MM_DD + " HH:mm:ss";

    /**
     * 日期与时间(12小时)
     */
    public static final String YYYY_MM_DD_12H_MM_SS = YYYY_MM_DD + " hh:mm:ss";


    /**
     * 一日的毫秒数
     */
    public static long MILLION_SECONDS_OF_DAY = 24 * 60 * 60 * 1000L;// 86400000
    /**
     * 一小时的毫秒数
     */
    public static long MILLION_SECONDS_OF_HOUR = 60 * 60 * 1000L;// 3600000;

    /**
     * 取得当前日期
     * @return
     */
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();//获取当前日期
        return calendar.getTime();
    }

    /**
     * 将当前日期转换为字符串
     * @param format 格式化字符串
     * @return
     */
    public static String getCurrentDate(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 当前日期
     * @return
     */
    public static String getCurrentDateYYYYMMDD() {

        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        return dateFormat.format(new Date());
    }

    /**
     * 当前日期
     * @return
     */
    public static String getCurrentDateYYYYMMDDHHMMSS() {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_24H_MM_SS);
        return dateFormat.format(new Date());
    }

    /**
     * 将指定日期转换为字符串
     *
     * @param date   日期
     * @param format 格式化字符串
     * @return
     */
    public static String getDate(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 通过时间获取字符串(YYYY-MM-DD HH:MM:SS)
     *
     * @return
     */
    public static String getDateYYYYMMDDHHMMSS(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_24H_MM_SS);
        return dateFormat.format(date);
    }

    /**
     * 通过时间获取字符串(YYYY-MM-DD)
     * @return
     */
    public static String getDateYYYYMMDD(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        return dateFormat.format(date);
    }

    /**
     * 解析日期字符串
     *
     * @param str 日期串
     * @param fmt 日期格式
     * @return
     */
    public static Date parse(String str, String fmt) {
        SimpleDateFormat simDateFormat = new SimpleDateFormat(fmt);
        Date date = null;
        try {
            date = simDateFormat.parse(str);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * string 转换 date
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseYYYYMMDDHHMMSS(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD_24H_MM_SS);
        return dateFormat.parse(time);
    }

    /**
     * 格式化输出日期
     *
     * @param time 需要格式化字符串
     * @return
     */
    public static String formatYYYYMMDDD(String time) {
        if (time == "") {
            return "";
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD);
        return df.format(time);
    }

    /**
     * 获取昨天日期
     *
     * @return
     */
    public static Date lastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }



    /**
     * 获取明天日期
     *
     * @return
     */
    public static Date nextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }




    /**
     * 获取明天的字符串表示
     *
     * @return
     */
    public static String lastDayYYYYMMDD() {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        return dateFormat.format(lastDay());
    }

    /**
     * 获取明天的字符串表示
     *
     * @return
     */
    public static String nextDayYYYYMMDD() {
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        return dateFormat.format(nextDay());
    }


//    /**
//     * 获取上周，周一、周天的日期数组
//     * 第一个是周一的日期
//     * 第二个是周天的日期
//     *
//     * @return
//     */
//    public static Date[] lastWeek() {
//        Date[] dates = new Date[2];
//        Calendar calendar = Calendar.getInstance();
//        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
//        calendar.add(Calendar.DAY_OF_MONTH, -(5 + dayWeek));
//        dates[0] = calendar.getTime();
//        calendar.add(Calendar.DAY_OF_MONTH, 6);
//        dates[1] = calendar.getTime();
//        return dates;
//    }
//
//    /**
//     * 获取本周，周一、周天的字符串数组
//     * 第一个是周一的日期字符串
//     * 第二个是周天的日期字符串
//     *
//     * @return
//     */
//    public static String[] thisWeekToString() {
//        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
//        Date[] dates = thisWeek();
//        String[] datesString = new String[2];
//        datesString[0] = dateFormat.format(dates[0]);
//        datesString[1] = dateFormat.format(dates[1]);
//        return datesString;
//    }
//
//    /**
//     * 获取本周，周一、周天的日期数组
//     * 第一个是周一的日期
//     * 第二个是周天的日期
//     *
//     * @return
//     */
//    public static Date[] thisWeek() {
//        Date[] dates = new Date[2];
//        Calendar calendar = Calendar.getInstance();
//        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
//        calendar.add(Calendar.DAY_OF_MONTH, -(dayWeek == 1 ? 6 : dayWeek - 2));
//        dates[0] = calendar.getTime();
//        calendar.add(Calendar.DAY_OF_MONTH, 6);
//        dates[1] = calendar.getTime();
//        return dates;
//    }
//
//    /**
//     * 获取本月，月头、月尾的字符串数组
//     * 第一个是月头的日期字符串
//     * 第二个是月尾的日期字符串
//     *
//     * @return
//     */
//    public static String[] thisMonthToString() {
//        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
//        Date[] dates = thisMonth();
//        String[] datesString = new String[2];
//        datesString[0] = dateFormat.format(dates[0]);
//        datesString[1] = dateFormat.format(dates[1]);
//        return datesString;
//    }
//
//    /**
//     * 获取本月，月头、月尾的日期数组
//     * 第一个是月头的日期
//     * 第二个是月尾的日期
//     *
//     * @return
//     */
//    public static Date[] thisMonth() {
//        Date[] dates = new Date[2];
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DATE) - 1));
//        dates[0] = calendar.getTime();
//        calendar.add(Calendar.MONTH, +1);
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        dates[1] = calendar.getTime();
//        return dates;
//    }
//
//    /**
//     * 获取下月，月头、月尾的字符串数组
//     * 第一个是月头的日期字符串
//     * 第二个是月尾的日期字符串
//     *
//     * @return
//     */
//    public static String[] lastMonthToString() {
//        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
//        Date[] dates = lastMonth();
//        String[] datesString = new String[2];
//        datesString[0] = dateFormat.format(dates[0]);
//        datesString[1] = dateFormat.format(dates[1]);
//        return datesString;
//    }
//
//    /**
//     * 获取下月，月头、月尾的日期数组
//     * 第一个是月头的日期
//     * 第二个是月尾的日期
//     *
//     * @return
//     */
//    public static Date[] lastMonth() {
//        Date[] dates = new Date[2];
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DATE)));
//        dates[1] = calendar.getTime();
//        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DATE) - 1));
//        dates[0] = calendar.getTime();
//        return dates;
//    }
//
//    /**
//     * 获取上周，周一、周天的字符串数组
//     * 第一个是周一的日期字符串
//     * 第二个是周天的日期字符串
//     *
//     * @return
//     */
//    public static String[] lastWeekToString() {
//        DateFormat dateFormat = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
//        Date[] dates = lastWeek();
//        String[] datesString = new String[2];
//        datesString[0] = dateFormat.format(dates[0]);
//        datesString[1] = dateFormat.format(dates[1]);
//        return datesString;
//    }

    /**
     * 取得当月的第一天
     *
     * @return
     */
    public static String getFirstDayOfMonth() {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_24H_MM_SS);
        Calendar calendar = Calendar.getInstance();//获取当前日期
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return format.format(calendar.getTime());
    }

    /**
     * 取得当月的最后一天
     *
     * @return
     */
    public static String getLastDayOfMonth() {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_24H_MM_SS);
        Calendar calendar = Calendar.getInstance();//获取当前日期
//        calendar.add(Calendar.MONTH, 1);
//        calendar.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(calendar.getTime());
    }

    /**
     * 判断当前日期是否是这个月的最后一天
     *
     * @param thisDay 当前日期
     * @return
     */
    public static boolean isLastDayOfMonth(Date thisDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +1);
        calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DATE)));
        DateFormat df = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        String lastDayStr = df.format(calendar.getTime());
        String thisDayStr = df.format(thisDay);
        return lastDayStr.equals(thisDayStr);
    }

    /**
     * 计算日期加月
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addMonth(Date date, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    /**
     * 计算日期加天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDay(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /**
     * 计算日期加分钟
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    /**
     * 计算天数差
     *
     * @param sd
     * @param ed
     * @return
     */
    public static int getDaysBetweenTwoDate(Date sd, Date ed) {
        Long eds = ed.getTime();
        Long sds = sd.getTime();
        return (int) ((eds - sds) / MILLION_SECONDS_OF_DAY);
    }

    /**
     * 计算天数差
     *
     * @param sd
     * @param ed
     * @return
     */
    public static int getDaysBetweenTwoDate(String sd, String ed) {
        Long eds = parse(ed, YYYY_MM_DD_24H_MM_SS).getTime();
        Long sds = parse(sd, YYYY_MM_DD_24H_MM_SS).getTime();
        return (int) ((eds - sds) / MILLION_SECONDS_OF_DAY);
    }





    /*SELECT * FROM oal_tb_action_logs al WHERE al.action_id = 'use_cabinet'
    AND  al.create_time BETWEEN '2014-11-27 00:00:00' and '2014-11-27 23:59:59'*/

    public static void main(String[] args) {
//        System.out.println(getMonthFirst());
//        System.out.println(getMonthLast());
//        SimpleDateFormat format = new SimpleDateFormat(BOTH);
//        Calendar calendar = Calendar.getInstance();//获取当前日期
//        System.out.println(format.format(calendar.getTime()));
//        List<String> list = null;
//        System.out.println(list.isEmpty());

    }

}
