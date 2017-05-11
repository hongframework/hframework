package com.hframework.web.interceptor;

import com.hframework.web.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Created by zqh on 2016/4/11.
 */
public class ThreadContextInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadContextInterceptor.class);


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler1) throws Exception {
        WebContext.clear();
        if(request.getSession() != null) {
            Enumeration attributeNames = request.getSession().getAttributeNames();
            if(attributeNames != null) {
                while (attributeNames.hasMoreElements()) {
                    String attrName = (String) attributeNames.nextElement();
                    Object attrValue = request.getSession().getAttribute(attrName);
                    WebContext.putSession(attrName, attrValue);
                }
            }
        }

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
