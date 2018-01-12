package com.hframework.web.controller.core;

import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2018/1/10.
 */
@Service
public class PageExtendDataManager {

    private static Map<String, HandlerMethod> urlMapping = new HashMap<String, HandlerMethod>();

    public Map<String, Object> getExtendData(String url, HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
        HandlerExecutionChain handler = null;
        if(urlMapping.size() == 0) {
            synchronized (PageExtendDataManager.class){
                Map<RequestMappingInfo, HandlerMethod> map = ServiceFactory.getService(RequestMappingHandlerMapping.class).getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
                    RequestMappingInfo info = m.getKey();
                    HandlerMethod method = m.getValue();
                    urlMapping.put(info.getPatternsCondition().getPatterns().iterator().next(), method);
                }
            }
        }

        HandlerMethod handlerMethod = urlMapping.get(url);
        if(handlerMethod == null) {
            return null;
        }

        try {
            Object bean = ServiceFactory.getService(String.valueOf(handlerMethod.getBean()));
            Method method = handlerMethod.getMethod();
            java.lang.Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if(ServletRequest.class.isAssignableFrom(parameterTypes[i])) {
                    parameters[i] = request;
                }else if(ServletResponse.class.isAssignableFrom(parameterTypes[i])) {
                    parameters[i] = response;
                }else if(ModelAndView.class.isAssignableFrom(parameterTypes[i])){
                    parameters[i] = mav;
                }
            }
            Object result = method.invoke(bean, parameters);
            return (Map<String, Object>)((ResultData)result).getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
