package com.hframework.common.springext.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/8/24.
 */
public class DataSourceContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);

    private static final ThreadLocal<DataSourceDescriptor> dbInfo = new ThreadLocal<DataSourceDescriptor>();

    private static final Map<String, DataSourceDescriptor> dbCache = new HashMap<String, DataSourceDescriptor>();

    private static DataSourceDescriptor defaultDataSourceDescriptor = new DataSourceDescriptor("dataSource","jdbc:mysql://localhost:3306/autosystem?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull","root","");

    public static  void setDbInfo(String url, String user, String password) {
        String key = url + "|"  + user + "|" + password;
        if(!dbCache.containsKey(key)) {
            dbCache.put(key,new DataSourceDescriptor(key, url,user,password));
        }
        DataSourceDescriptor dataSourceDescriptor = dbCache.get(key);
        dbInfo.set(dataSourceDescriptor);
    }

    public static DataSourceDescriptor getDBInfo() {
        DataSourceDescriptor dataSourceDescriptor = dbInfo.get();
        return dataSourceDescriptor;
    }

    public static DataSourceDescriptor getDBInfoAnyMore() {
        DataSourceDescriptor dataSourceDescriptor = dbInfo.get();
        if(dataSourceDescriptor == null) {
            return defaultDataSourceDescriptor;
        }
        return dataSourceDescriptor;
    }

    public static void clear(){
        dbInfo.remove();
    }

    public static class DataSourceDescriptor{
         public String key;
         public String url;
         public String user;
         public String password;

        public DataSourceDescriptor(String key, String url,String user,String password){
            this.key = key;
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    public static DataSourceDescriptor getDefaultDataSourceDescriptor() {
        return defaultDataSourceDescriptor;
    }

    public static void setDefaultDataSourceDescriptor(DataSourceDescriptor defaultDataSourceDescriptor) {
        DataSourceContextHolder.defaultDataSourceDescriptor = defaultDataSourceDescriptor;
    }
}
