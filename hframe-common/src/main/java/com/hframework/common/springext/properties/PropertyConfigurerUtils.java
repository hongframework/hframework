package com.hframework.common.springext.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyConfigurerUtils extends PropertyPlaceholderConfigurer {

    private static Map<String, String> propertiesMap = new HashMap<String, String>();

    @Override
    protected void processProperties( ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            propertiesMap.put(keyStr, value);
        }
    }

    /**
     * 根据Property的Key，获取相应的消息内容
     * @param name 键值
     * @return 字符串
     */
    public static String getProperty(String name) {
        return propertiesMap.get(name);
    }

    public static boolean containProperty(String name) {
        return propertiesMap.containsKey(name);
    }

    /**
     * 根据Property的Key，获取相应的消息内容，并替换占位符相应内容
     * @param key 键值
     * @param args 占位符对应参数
     * @return 字符串
     */
    public static String getProperty(String key, Object... args) {
        if(containProperty(key)) {
            return MessageFormat.format(getProperty(key), args);
        }else {
            return null;
        }

    }

}
