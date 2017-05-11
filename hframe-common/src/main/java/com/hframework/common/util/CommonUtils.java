package com.hframework.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 14-4-29.
 */
public class CommonUtils {

    private static Random random = new Random();
    private static long index = 2631367; // 1102569 后是假数据

    public static String getRandomCode(int begin, int end) {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }

        StringBuilder result = new StringBuilder();
        for (int i = begin; i < end; i++) {
            result.append(array[i]);
        }
        return result.toString();
    }

    /**
     * 获取用户推荐码
     * 
     * return String
     */
    public static String getRecommendCode() {
        return "0000" + CommonUtils.getRandomCode(0, 6);
    }

/*    public static void main(String args[]) {
        System.out.println(CommonUtils.getRecommendCode().substring(4, 10));
    }*/

    /**
     * 生成订单支付编号
     * 
     * return String
     */
    public static String createPayCode(String header, String cabinetCode, String boxNum) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String nowTime = simpleDateFormat.format(new Date());
        String payCode;
        if (StringUtils.isNotBlank(header)) {
            payCode = header + "_" + cabinetCode + boxNum + nowTime;
        } else {
            payCode = cabinetCode + boxNum + nowTime;
        }
        return payCode;
    }

    /**
     * 获取19位序列号 (12位时间戳+4位递增序号+3位随机数)
     *
     * @return
     */
    /*public synchronized static String next() {
        Date date = Calendar.getInstance().getTime();
		String dateStr = DateHelper.format(date, "yyMMddhhmmss");
		return dateStr + increase() + random(3);
	}*/
    public synchronized static String uuid() {
        Date date = Calendar.getInstance().getTime();
        String dateStr = DateUtils.getDate(date, "yyMMddhhmmss");
        String seq = dateStr + increase() + random(3);
//        return Long.valueOf(seq);
        return seq;
    }

    public synchronized static Long uuidL() {
        Date date = Calendar.getInstance().getTime();
        String dateStr = DateUtils.getDate(date, "yMMdd");
        String seq = dateStr + random(6);
        return Long.valueOf(seq);
    }

    public static String getSequencNo() {
        return Long.toString(new Date().getTime() / 1000) + random(4);
    }

    public static synchronized String increase() {
        String currIndex = String.valueOf(index);
        for (int i = currIndex.length(); i < 4; i++) {
            currIndex = "0" + currIndex;
        }
        if (index == 999999999) {
            index = 1;
        } else {
            index += 1;
        }
        return currIndex;
    }

    public static String random(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int tempval = (int) ((float) '0' + random.nextFloat() * (float) ('9' - '0'));
            buffer.append(new Character((char) tempval));
        }
        return buffer.toString();
    }

//    /**
//     * 获取远程访问IP
//     *
//     * @return
//     */
//    public static String getRemoteIp() {
//        // 获取请求
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String remoteIp = request.getHeader("x-forwarded-for");
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getHeader("X-Real-IP");
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getHeader("Proxy-Client-IP");
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getRemoteAddr();
//        }
//        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
//            remoteIp = request.getRemoteHost();
//        }
//        return remoteIp;
//    }

    public static void main(String[] args) {
        System.out.println(Long.valueOf("1509230954232631367858"));
    }
}
