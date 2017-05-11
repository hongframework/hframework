package com.hframework.common.util;

//import com.ourangel.box.common.mysql.MySqlFieldType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    /**
     * 首字母转大写
     *
     * @param str 转换的字符串
     * @return 转换后字符串
     */
    public static String upperCaseFirstChar(String str) {
        if (StringUtils.isBlank(str)) {
            throw new NullPointerException("String param is null");
        }
        String first = (str.substring(0, 1)).toUpperCase();
        String other = str.substring(1);
        return first + other;
    }

    /**
     * 首字母转大写
     *
     * @param str 转换的字符串
     * @return 转换后字符串
     */
    public static String lowerCaseFirstChar(String str) {
        if (StringUtils.isBlank(str)) {
            throw new NullPointerException("String param is null");
        }
        String first = (str.substring(0, 1)).toLowerCase();
        String other = str.substring(1);
        return first + other;
    }

    /**
     * 判断一个字符串是否转换为非负整数
     *
     * @param str 字符串
     * @return 真假
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 根据表名按规则生成对应的类名称
     * 如果有前缀，则从前缀后开始生成类名
     * 如果有后缀，则从后缀前开始生成类名
     *
     * @param tableName 表名称
     * @param prefix    前缀
     * @param suffix    后缀
     * @param isCase    是否转换小写
     * @return
     */
    public final static String getClassName(String tableName, String prefix, String suffix, boolean isCase) {
        // 断判是否有前缘需要处理
        if (org.apache.commons.lang.StringUtils.isNotBlank(prefix)) {
            int pIndex = tableName.indexOf(prefix);
            if (pIndex > -1) {
                tableName = tableName.substring(prefix.length(), tableName.length());
            }
        }
        // 断判是否有后缀需要处理
        if (org.apache.commons.lang.StringUtils.isNotBlank(suffix)) {
            int sIndex = tableName.indexOf(suffix);
            if (sIndex > -1) {
                tableName = tableName.substring(0, tableName.length() - suffix.length());
            }
        }
        // 处理表名转换为类名，去掉下划线，下划线后首字母大写
        tableName = porssClassName(tableName, isCase);
        // 首字母
        String upperChar = tableName.substring(0, 1);
        // 首字母转大写加上首位后的字符
        tableName = org.apache.commons.lang.StringUtils.upperCase(upperChar) + tableName.substring(1, tableName.length());

        return tableName;
    }

    /**
     * 根据表名按规则生成对应的类名称
     *
     * @param tableName 表名称
     * @param isCase    是否转换小写
     * @return
     */
    public final static String getClassName(String tableName, boolean isCase) throws Exception {
        return getClassName(tableName, null, null, isCase);
    }

    public final static String porssClassName(String tableName, boolean isCase) {
        if (isCase) {
            tableName = org.apache.commons.lang.StringUtils.lowerCase(tableName);
        }

        int index = tableName.indexOf("_");
        if (index > 0) {
            StringBuilder buffer = new StringBuilder(tableName.length());
            buffer.append(tableName.substring(0, index));
            String upperStr = tableName.substring(index + 1, index + 2);
            buffer.append(org.apache.commons.lang.StringUtils.upperCase(upperStr));
            buffer.append(tableName.substring(index + 2, tableName.length()));
            tableName = buffer.toString();
            index = tableName.indexOf("_");
            if (index > 0) {
                tableName = porssClassName(tableName, false);
            }
        }
        return tableName;
    }

//    /**
//     * 获取字段对应JAVA类型
//     *
//     * @param sqlType 数据库类型
//     * @return
//     */
//    public final static String getFieldTypeByMsql(String sqlType) {
//        return MySqlFieldType.valueOf(sqlType).getJavaType();
//    }

    /**
     * 获取首字母大写的名称
     *
     * @param name 名称
     * @return
     */
    public final static String getFieldName(String name) {
        StringBuilder buffer = new StringBuilder(name.length());
        buffer.append(org.apache.commons.lang.StringUtils.upperCase(name.substring(0, 1)));
        buffer.append(name.substring(1, name.length()));
        return name;
    }

    /**
     * 获取以分割符分割的转换后字段名称（各分割后的首字线大写）
     *
     * @param name  名称
     * @param split 分割符
     * @return
     */
    public final static String getFieldName(String name, String split) {
        String[] names = name.split(split);
        StringBuilder buffer = new StringBuilder(name.length());
        for (String str : names) {
            buffer.append(getFieldName(str));
        }
        return name;
    }

    /**
     * 字符名称是否需要转小写
     *
     * @param name   名称
     * @param isCase 是否转小写
     * @return
     */
    public final static String getFieldName(String name, boolean isCase) {
        if (isCase) {
            name = org.apache.commons.lang.StringUtils.lowerCase(name);
        }
        return getFieldName(name);
    }

    /**
     * 字符名称是否需要转小写，并以分割符进行分割转换
     *
     * @param name   名称
     * @param isCase 是否转小写
     * @param split  分割符
     * @return
     */
    public final static String getFieldName(String name, boolean isCase, String split) {
        if (isCase) {
            name = org.apache.commons.lang.StringUtils.lowerCase(name);
        }
        return getFieldName(name, split);
    }

    /**
     * 获取UUID字符串，去除－符号
     *
     * @return
     */
    public final static String getUUID() {
        String uuidTemp = UUID.randomUUID().toString();
        // 去掉"-"符号
        String uuid = uuidTemp.substring(0, 8) + uuidTemp.substring(9, 13) + uuidTemp.substring(14, 18) + uuidTemp.substring(19, 23) + uuidTemp.substring(24);
        return uuid;
    }

    /**
     * 判断一个字符串是否为中文
     * @author caojingxin
     * @param name
     * @return
     */
    public static boolean isChineseName(String name) {

        Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]){2,5}$");

        Matcher matcher = pattern.matcher(name);

        if(matcher.find()){

            return true;
        }
        return false;
    }

    /**
     * 转Byte[]
     * @param inStream
     * @return
     * @throws Exception
     */

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
