package com.hframework.common.helper;

import com.hframework.common.util.BeanUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.security.MD5Util;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by zqh on 2016/4/19.
 */
public class Rules {

    public static String getTimeStamp() throws Exception {
        return (System.currentTimeMillis() / 1000) + "";
    }

    public static String randomChar32() throws Exception {
        return MD5Util.encrypt(String.valueOf(Math.random() * 10000));
    }

    public static String signAllNotEmptyParams(Object object,String extendString) throws Exception {
        Map<String, String> notEmptyAttrParamMap = null;
        if (object instanceof Map) {
            notEmptyAttrParamMap = (Map) object;
        }else {
            notEmptyAttrParamMap = BeanUtils.convertMap(object, false);
        }
        String buff = null;
        try {
            buff = formatBizQueryParaMap(notEmptyAttrParamMap,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(extendString)) {
            buff += extendString;
        }
        return MD5Util.encrypt(buff).toUpperCase();
    }

    public static String signAllParams(Object object,String extendString) throws Exception {
        Map<String, String> notEmptyAttrParamMap = null;
        if (object instanceof Map) {
            notEmptyAttrParamMap = (Map) object;
        }else {
            notEmptyAttrParamMap = BeanUtils.convertMap(object, false);
        }
        String buff = null;
        try {
            buff = formatBizQueryParaMap(notEmptyAttrParamMap,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(extendString)) {
            buff += extendString;
        }
        System.out.println("-------------" + buff);
        System.out.println("-------------" + MD5Util.encrypt(buff));
        return MD5Util.encrypt(buff);
    }

    public static String checkAllNotEmptyParamsSign(Object object,String extendString) throws Exception {
        Map<String, String> notEmptyAttrParamMap = BeanUtils.convertMap(object, false);
        String sign = notEmptyAttrParamMap.get("sign");
        notEmptyAttrParamMap.remove("sign");
        String buff = null;
        try {
            buff = formatBizQueryParaMap(notEmptyAttrParamMap,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(extendString)) {
            buff += extendString;
        }

        String rightSign = MD5Util.encrypt(buff).toUpperCase();

        if(!rightSign.equals(sign)) {
            throw new Exception("sign check Exception [" + sign + " | " + rightSign + "]!");
        }

        return "true";
    }


    /**
     * 与上述方法相同，根据不同的编码方式编码
     *
     * @param paraMap
     * @param urlencode
     * @return
     * @throws Exception
     */
    public static String formatBizQueryParaMap(Map<String, String> paraMap, boolean urlencode) throws Exception {
        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(paraMap.entrySet());
            Collections.sort(infoIds,
                    new Comparator<Map.Entry<String, String>>() {
                        public int compare(Map.Entry<String, String> o1,
                                           Map.Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });
            for (int i = 0; i < infoIds.size(); i++) {
                Map.Entry<String, String> item = infoIds.get(i);
                //System.out.println(item.getKey());
                if (item.getKey() != "") {

                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");

                    }
                    buff += key.toLowerCase() + "=" + val + "&";

                }
            }
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buff;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(randomChar32());
    }
}
