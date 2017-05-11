package com.hframework.common.util.message;

import java.util.*;

/**
 * Created by zhangquanhong on 2017/4/27.
 */
public class PropertyReader {

    private String path;

    private List<String> propertyNameDefines = new ArrayList<String>();

    private Map<String, List<String>> propertyDefineAndExtName = new LinkedHashMap<String, List<String>>();

    private Map<String, String> propertyNameAndValue = new HashMap<String, String>();

    private List<String> unknownPropertyName = new ArrayList<String>();

    private boolean beenGroup = false;

    public PropertyReader addDefine(String propertyName) {
        propertyNameDefines.add(propertyName);
        return this;
    }

    public PropertyReader addDefine(String... propertyName) {
        propertyNameDefines.addAll(Arrays.asList(propertyName));
        return this;
    }

    public Map<String, String> getAsList(String propertyName) {
        if(!beenGroup) {
            synchronized (this) {
                if(!beenGroup) {
                    group();
                    beenGroup = true;
                }
            }
        }

        Map<String, String> result = new HashMap<String, String>();
        if(propertyDefineAndExtName.containsKey(propertyName)) {
            List<String> extNames = propertyDefineAndExtName.get(propertyName);
            for (String extName : extNames) {
                result.put(extName, propertyNameAndValue.get(propertyName + extName));
            }
        }else {
            if(propertyNameAndValue.containsKey(propertyName)) {
                result.put("", propertyNameAndValue.get(propertyName));
            }
        }
        return result;
    }

    private void group() {
        Collections.sort(propertyNameDefines);
        Collections.reverse(propertyNameDefines);
        for (Map.Entry<String, String> nameAndValue : propertyNameAndValue.entrySet()) {
            String key = nameAndValue.getKey();
            if(!propertyNameDefines.contains(key)) {
                for (String propertyNameDefine : propertyNameDefines) {
                    if(key.startsWith(propertyNameDefine)) {
                        if(!propertyDefineAndExtName.containsKey(propertyNameDefine)) {
                            propertyDefineAndExtName.put(propertyNameDefine, new ArrayList<String>());
                        }
                        propertyDefineAndExtName.get(propertyNameDefine).add(key.substring(propertyNameDefine.length()));
                        break;
                    }
                }
                unknownPropertyName.add(key);
            }
        }
    }

    public Integer getAsInt(String propertyName, Integer defaultValue) {
        if(propertyNameAndValue.get(propertyName) != null) {
            return Integer.valueOf(propertyNameAndValue.get(propertyName).trim());
        }else {
            return defaultValue;
        }
    }

    public Boolean getAsBoolean(String propertyName, boolean defaultValue) {
        if(propertyNameAndValue.get(propertyName) != null) {
            return Boolean.valueOf(propertyNameAndValue.get(propertyName).trim());
        }else {
            return defaultValue;
        }
    }

    public Long getAsLong(String propertyName, Long defaultValue) {
        if(propertyNameAndValue.get(propertyName) != null) {
            return Long.valueOf(propertyNameAndValue.get(propertyName).trim());
        }else {
            return defaultValue;
        }
    }
    public String get(String propertyName, String defaultValue) {
        if(propertyNameAndValue.get(propertyName) != null) {
            return propertyNameAndValue.get(propertyName).trim();
        }else {
            return defaultValue;
        }
    }
    public String get(String propertyName) {
        if(propertyNameAndValue.get(propertyName) != null) {
            return propertyNameAndValue.get(propertyName).trim();
        }else {
            return null;
        }
    }

    public PropertyReader merge(String resourceName) {
        try{
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceName.replace(".properties",""));
            if(resourceBundle == null) return this;
            return mergeProperty(this, resourceBundle);
        }catch (Exception e ) {
            e.printStackTrace();
            return  this;
        }

    }

    public static PropertyReader read(String resourceName){
        PropertyReader propertyContext = new PropertyReader();
        try{
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceName.replace(".properties",""));
            if(resourceBundle == null) return propertyContext;
            propertyContext.setPath(resourceName);
            return mergeProperty(propertyContext, resourceBundle);
        }catch (Exception e) {
            e.printStackTrace();
            return propertyContext;
        }
    }

    private static PropertyReader mergeProperty(PropertyReader propertyContext, ResourceBundle resourceBundle) {
        Enumeration<String> keys = resourceBundle.getKeys();
        while(keys.hasMoreElements()) {
            String elementName = keys.nextElement();
            String elementValue = resourceBundle.getString(elementName);
            propertyContext.put(elementName, elementValue);
        }
        return propertyContext;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void put(String key, String value) {
        propertyNameAndValue.put(key,value);
    }
}
