package com.hframework.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2017/4/20.
 */
public class JavaUtil {
    private static final Map<String,String> KEYWORDS= new HashMap<String, String>() {{
        put("interface","interface1");
        put("class","clazz");
    }};


    public static String getJavaClassName(String name) {

        String returnName = "";

        String[] parts = name.split("[_\\-\\.]+");
        for (String part : parts) {
            if (!"".equals(part)) {
                returnName += part.substring(0, 1).toUpperCase()
                        + part.substring(1);
            }
        }

        return returnName;
    }

    public static String getJavaVarName(String name) {

        String returnName="";

        String[] parts=name.split("[__\\-\\.]+");
        for (String part : parts) {
            if(!"".equals(part)){
                returnName+=part.substring(0,1).toUpperCase()+part.substring(1);
            }
        }
        String javaVarName = null;
        if(StringUtils.isBlank(returnName)) {
            System.out.println("==>" + name);
        }
        if(returnName == null ||returnName.length() < 1) {
            System.out.println();
        }
        javaVarName = returnName.substring(0, 1).toLowerCase() + returnName.substring(1);


        if(KEYWORDS.containsKey(javaVarName)) {
            return KEYWORDS.get(javaVarName);
        }

        return javaVarName;
    }
}