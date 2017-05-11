package com.hframework.common.springext.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/8/24.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    private static Map<Object, Object> dataSourcePool = new HashMap<Object, Object>();

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceContextHolder.DataSourceDescriptor dbInfo = DataSourceContextHolder.getDBInfo();
        if(dbInfo == null) {

        }else {
            try {
                selectAndCreateIfNotExists(dbInfo);
                logger.info("==> datasource :{}",dbInfo.url);
                return dbInfo.key;
            } catch (PropertyVetoException e) {
                logger.error("select datasource error :", e);
            }
        }
        logger.info("==> datasource :{}","default");
        return null;
    }

    private void selectAndCreateIfNotExists(DataSourceContextHolder.DataSourceDescriptor dbInfo) throws PropertyVetoException {
        if(!dataSourcePool.containsKey(dbInfo.key)) {
            dataSourcePool.put(dbInfo.key, createDataSource(dbInfo.url, dbInfo.user, dbInfo.password));
        }
        super.setTargetDataSources(dataSourcePool);
        super.afterPropertiesSet();
    }

    private DataSource createDataSource(String url, String user, String password) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setMinPoolSize(2);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(20);
        dataSource.setMaxIdleTime(300);
        return dataSource;
    }
}
