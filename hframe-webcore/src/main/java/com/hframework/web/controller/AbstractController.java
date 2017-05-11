package com.hframework.web.controller;

import com.hframework.common.util.RegexUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2017/2/10.
 */
public abstract class AbstractController {

    /**
     * 解析参数从dataCondition
     * @param request
     * @return
     */
    public Map<String, String> parseParameterForDataCondition(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<String, String>();
        String dataCondition = request.getParameter("dataCondition");
        if(StringUtils.isNotBlank(dataCondition)) {
            String[] keyValuePairs = RegexUtils.find(dataCondition, "[^=&]+=[^=&]*");
            for (String keyValuePair : keyValuePairs) {
                if(keyValuePair.endsWith("=")) {
                    parameters.put(keyValuePair.split("=")[0].trim(), "");
                }else {
                    parameters.put(keyValuePair.split("=")[0].trim(), keyValuePair.split("=")[1].trim());
                }
            }
        }
        return parameters;
    }
}
