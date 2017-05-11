package com.hframework.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/4/21.
 */
public class UrlHelper {

    public static String getFinalUrl(String patternUrl, Map<String, String> parameterMap) {
        String finalUrl = patternUrl;
        if(finalUrl.contains("?")) {
            finalUrl = finalUrl.substring(0,finalUrl.indexOf("?")) + "?";
        }
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            finalUrl+= entry.getKey() + "=" + entry.getValue() + "&";
        }

        if(finalUrl.endsWith("&")) {
            finalUrl = finalUrl.substring(0,finalUrl.length() - 1);
        }
        return finalUrl;
    }

    public static String getUrlPath(String patternUrl) {
        String finalUrl = patternUrl;
        if(finalUrl.contains("?")) {
            finalUrl = finalUrl.substring(0,finalUrl.indexOf("?"));
        }
        return finalUrl;
    }


    public static Map<String, String> getUrlParameters(String url,boolean javaNamespaceKey) {
        Map<String, String> map = new HashMap<String, String>();
        if(url.contains("?")) {
            url = url.substring(url.indexOf("?") + 1);
            String[] params = url.split("&");
            if(params == null) {
                return map;
            }
            for (String param : params) {
                if(param.contains("=")) {
                    map.put(javaNamespaceKey ? JavaUtil.getJavaVarName(param.substring(0, param.indexOf("=")))
                            : param.substring(0, param.indexOf("=")), param.substring(param.indexOf("=") + 1).trim());
                }
            }
        }
        return map;
    }

    public static String getUrlQueryString(Map<String, String> paramMap) {
        StringBuffer sb = new StringBuffer();
        for (String key : paramMap.keySet()) {
            if(StringUtils.isBlank(paramMap.get(key))) {
                continue;
            }
            if(sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(paramMap.get(key));
        }
        return sb.toString();
    }

}
