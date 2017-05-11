package com.hframework.common.frame;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Created by zhangqh6 on 2015/10/20.
 * 服务工厂
 */
@Repository
public class ServiceFactory implements ApplicationContextAware {

    private static ApplicationContext webappcontext = null;

    public static Object getService(String beanName) {
        if(webappcontext == null) {
            try {
                initContext(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return webappcontext.getBean(beanName);
    }

    public static <T> T getService(Class<T> clazz) {
        if(webappcontext == null) {
            try {
                initContext(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return webappcontext.getBean(clazz);
    }

    public static void initContext(ApplicationContext applicationContext) throws Exception{
        if(applicationContext == null) {
            throw new Exception("ServiceFactory init error");
        }
        webappcontext = applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        webappcontext = applicationContext;
    }

    public static void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException{
        webappcontext.getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
    }

    public static ApplicationContext getWebAppContext() {
        return webappcontext;
    }
}
